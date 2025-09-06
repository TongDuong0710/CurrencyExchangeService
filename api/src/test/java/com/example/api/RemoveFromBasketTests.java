package com.example.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
/**
 * Scenario: Remove product from basket should restore stock and clear from receipt.
 *
 * Steps:
 * 1. Admin seeds a product with stock = 5.
 * 2. Customer adds 2 units into basket → stock decreases to 3.
 * 3. Customer removes the product from basket via DELETE API.
 * 4. System restores stock back to 5.
 * 5. Receipt becomes empty (no line item for that product, total = 0).
 *
 * Purpose: Verify basket removal works correctly, stock consistency is maintained,
 * and no stale items remain in the receipt.
 */
class RemoveFromBasketTests extends BaseE2E {

    private static final String CUSTOMER_ID = "user-remove";

    @Test
    void remove_item_should_restore_stock_and_clear_from_receipt() {
        // Seed product stock=5
        Long pid = given().contentType(ContentType.JSON).body("""
            {"name":"Removable","description":"d","category":"ACC","price":50.0,"stock":5}
        """).when().post("/api/admin/products")
                .then().statusCode(200)
                .extract().jsonPath().getLong("data.id");

        // Add 2 to basket
        given().header("X-Customer-ID", CUSTOMER_ID).contentType(ContentType.JSON).body("""
            {"productId": %d, "quantity": 2}
        """.formatted(pid)).when().post("/api/customer/basket/items")
                .then().statusCode(200);

        // Stock now = 3
        given().queryParam("page", 0).queryParam("size", 10)
                .when().get("/api/admin/products")
                .then().statusCode(200)
                .body("data.content.find { it.id == %d }.stock".formatted(pid), is(3));

        // Remove item entirely
        given().header("X-Customer-ID", CUSTOMER_ID)
                .when().delete("/api/customer/basket/items/{productId}", pid)
                .then().statusCode(200)
                .body("status.code", is("ES-0000"));

        // Stock restored to 5
        given().queryParam("page", 0).queryParam("size", 10)
                .when().get("/api/admin/products")
                .then().statusCode(200)
                .body("data.content.find { it.id == %d }.stock".formatted(pid), is(5));

        // Receipt empty
        given().header("X-Customer-ID", CUSTOMER_ID)
                .when().get("/api/customer/receipt")
                .then().statusCode(200)
                .body("data.items.find { it.productId == %d }".formatted(pid), nullValue())
                .body("data.totalFinal.toString()", anyOf(is("0.0"), is("0")));
    }
}
