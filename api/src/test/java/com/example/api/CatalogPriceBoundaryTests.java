package com.example.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Catalog boundary filter:
 * minPrice == maxPrice == product.price -> the product should appear.
 */
class CatalogPriceBoundaryTests extends BaseE2E {

    @Test
    void catalog_should_include_items_on_exact_price_boundary() {
        // Seed product with price=1000
        given().contentType(ContentType.JSON).body("""
            {"name":"Boundary","description":"d","category":"ACC","price":1000.00,"stock":5}
        """).when().post("/api/admin/products").then().statusCode(200);

        // Filter with min=max=1000
        given().queryParam("page", 0).queryParam("size", 5)
                .queryParam("category", "ACC")
                .queryParam("minPrice", 1000)
                .queryParam("maxPrice", 1000)
                .queryParam("available", true)
                .when().get("/api/customer/products")
                .then().statusCode(200)
                .body("status.code", is("ES-0000"))
                .body("data.number", is(0))
                .body("data.size", is(5))
                .body("data.totalElements", greaterThanOrEqualTo(1))
                .body("data.content.find { it.name == 'Boundary' }.price", is(1000.00F));
    }
}
