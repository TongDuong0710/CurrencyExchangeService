package com.example.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

/**
 * E2E: Insufficient stock
 * 1) Seed a product with stock=1.
 * 2) Add quantity=2 -> expect 409 Conflict.
 * 3) Verify stock unchanged (=1) via admin listing.
 */
class InsufficientStockTests extends BaseE2E {

    Long productId;

    @BeforeEach
    void seed_low_stock_product() {
        productId =
                given()
                        .contentType(ContentType.JSON)
                        .body("""
                      {
                        "name":"LowStock",
                        "description":"demo",
                        "category":"PHONE",
                        "price":100.00,
                        "stock":1
                      }
                      """)
                        .when().post("/api/admin/products")
                        .then()
                        .statusCode(200)
                        .extract()
                        .jsonPath().getLong("data.id");
    }

    @Test
    void adding_more_than_stock_should_fail_and_not_change_stock() {
        // Try to add 2 while stock=1
        given()
                .contentType(ContentType.JSON)
                .body("""
                  {"productId": %d, "quantity": 2}
                  """.formatted(productId))
                .when().post("/api/customer/basket/items")
                .then()
                .statusCode(409); // Conflict: insufficient stock

        // Stock unchanged (admin list uses data.content)
        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("name", "LowStock")
                .when().get("/api/admin/products")
                .then()
                .statusCode(200)
                .body("data.content.find { it.id == %d }.stock".formatted(productId), is(1));
    }
}
