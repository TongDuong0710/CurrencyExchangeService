package com.example.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static io.restassured.RestAssured.given;

/**
 * Concurrency stress test: multiple customers try to add the same product simultaneously.
 * Ensures the system does not oversell stock (successes <= initial stock),
 * rejects excess requests with 409 Conflict,
 * and the final stock matches initialStock - successful purchases.
 */
class ConcurrentAddStockTests extends BaseE2E {

    @Test
    void many_customers_compete_for_limited_stock_no_oversell() throws Exception {
        final int initialStock = 20;
        final int threads = 50;

        // Seed product
        Long pid = given().contentType(ContentType.JSON).body("""
        {"name":"HotItem","description":"d","category":"PHONE","price":99.0,"stock":%d}
    """.formatted(initialStock)).when().post("/api/admin/products")
                .then().statusCode(200)
                .extract().jsonPath().getLong("data.id");

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch startGun = new CountDownLatch(1);
        AtomicInteger success = new AtomicInteger();
        AtomicInteger conflict = new AtomicInteger();
        AtomicInteger other = new AtomicInteger();

        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            final String customerId = "concurrent-user-" + i;
            futures.add(pool.submit(() -> {
                try {
                    startGun.await();
                    int status = given()
                            .header("X-Customer-ID", customerId)
                            .contentType(ContentType.JSON)
                            .body("""
                            {"productId": %d, "quantity": 1}
                        """.formatted(pid))
                            .when().post("/api/customer/basket/items")
                            .then().extract().statusCode();

                    if (status == 200) success.incrementAndGet();
                    else if (status == 409) conflict.incrementAndGet();
                    else other.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));
        }

        // Fire all
        startGun.countDown();
        for (Future<?> f : futures) f.get(30, TimeUnit.SECONDS);
        pool.shutdown();

        // Invariants
        int succ = success.get();
        int conf = conflict.get();
        int oth = other.get();

        org.junit.jupiter.api.Assertions.assertEquals(0, oth, "unexpected status codes besides 200/409");
        org.junit.jupiter.api.Assertions.assertEquals(threads, succ + conf, "all requests accounted for");
        org.junit.jupiter.api.Assertions.assertTrue(succ <= initialStock, "must not oversell beyond stock");

        // === Final stock via GET-by-id ===
        Integer finalStock = given()
                .when().get("/api/admin/products/{id}", pid)
                .then().statusCode(200)
                .extract().jsonPath().getObject("data.stock", Integer.class);

        org.junit.jupiter.api.Assertions.assertNotNull(finalStock, "data.stock is missing");
        org.junit.jupiter.api.Assertions.assertEquals(initialStock - succ, finalStock.intValue(),
                "final stock matches consumed units");
    }

}
