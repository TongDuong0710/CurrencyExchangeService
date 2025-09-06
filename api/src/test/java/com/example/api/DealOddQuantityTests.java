package com.example.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Odd-quantity case for B1G50_2ND:
 * qty=3 ⇒ only one pair gets 50% off; the 3rd item no discount.
 * Expect: unit=100, original=100*3=300, discount=50, final=250.
 */
class DealOddQuantityTests extends BaseE2E {

    private static final String CUSTOMER_ID = "user-deal-odd";

    @Test
    void b1g50_second_odd_quantity_should_discount_pairs_only() {
        // Seed product (price=100, stock=10)
        Long pid = given().contentType(ContentType.JSON).body("""
            {"name":"OddCase","description":"d","category":"PHONE","price":100.00,"stock":10}
        """).when().post("/api/admin/products").then().statusCode(200)
                .extract().jsonPath().getLong("data.id");

        // Create future B1G50_2ND
        given().contentType(ContentType.JSON).body("""
            {"productId": %d, "dealType": "B1G50_2ND", "expirationDateTime":"2030-12-31T23:59:59"}
        """.formatted(pid)).when().post("/api/admin/deals").then().statusCode(200)
                .body("status.code", is("ES-0000"));

        // Add qty=3
        given().header("X-Customer-ID", CUSTOMER_ID).contentType(ContentType.JSON).body("""
            {"productId": %d, "quantity": 3}
        """.formatted(pid)).when().post("/api/customer/basket/items")
                .then().statusCode(200).body("status.code", is("ES-0000"));

        // Receipt: original=300, discount=50 (50% of 1 unit), final=250
        given().header("X-Customer-ID", CUSTOMER_ID)
                .when().get("/api/customer/receipt")
                .then().statusCode(200).body("status.code", is("ES-0000"))
                .body("data.items[0].productId", is(pid.intValue()))
                .body("data.items[0].quantity", is(3))
                .body("data.items[0].unitPrice.toString()", is("100.0"))
                .body("data.items[0].originalSubtotal.toString()", is("300.0"))
                .body("data.items[0].discount.toString()", is("50.0"))
                .body("data.items[0].finalSubtotal.toString()", is("250.0"))
                .body("data.items[0].appliedDeal", is(true))
                .body("data.totalBeforeDiscount.toString()", is("300.0"))
                .body("data.totalDiscount.toString()", is("50.0"))
                .body("data.totalFinal.toString()", is("250.0"));
    }
}
