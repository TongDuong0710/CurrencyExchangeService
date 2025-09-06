package com.example.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
/**
 * E2E Test: verifies that when an admin deletes a product,
 * it is removed from both the admin catalog and the customer catalog.
 */
class AdminDeleteProductTests extends BaseE2E {

    @Test
    void delete_product_should_remove_from_admin_and_customer_catalog() {
        // Seed product
        Long id = given().contentType(ContentType.JSON).body("""
              {"name":"ToDelete","description":"d","category":"ACC","price":10.00,"stock":5}
            """).when().post("/api/admin/products")
                .then().statusCode(200).body("status.code", is("ES-0000"))
                .extract().jsonPath().getLong("data.id");

        // Delete
        given().when().delete("/api/admin/products/{id}", id)
                .then().statusCode(200).body("status.code", is("ES-0000"));

        // Not in admin list anymore
        given().queryParam("page", 0).queryParam("size", 10)
                .when().get("/api/admin/products")
                .then().statusCode(200)
                .body("data.content.find { it.id == %d }".formatted(id), nullValue());

        // Not visible to customer
        given().queryParam("page", 0).queryParam("size", 10)
                .when().get("/api/customer/products")
                .then().statusCode(200)
                .body("data.content.find { it.id == %d }".formatted(id), nullValue());
    }
}
