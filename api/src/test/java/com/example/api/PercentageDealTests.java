package com.example.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Percentage deal case (THIRTY_PERCENT_OFF):
 * unit=200, qty=2 ⇒ original=400, discount=120 (30%), final=280.
 */
class PercentageDealTests extends BaseE2E {

    private static final String CUSTOMER_ID = "user-deal-percent";

    @Test
    void thirty_percent_off_should_reduce_line_and_totals() {
        // Seed product (price=200)
        Long pid = given().contentType(ContentType.JSON).body("""
            {"name":"PCT","description":"d","category":"PHONE","price":200.00,"stock":10}
        """).when().post("/api/admin/products").then().statusCode(200)
                .extract().jsonPath().getLong("data.id");

        // Create future THIRTY_PERCENT_OFF
        given().contentType(ContentType.JSON).body("""
            {"productId": %d, "dealType": "THIRTY_PERCENT_OFF", "expirationDateTime":"2030-12-31T23:59:59"}
        """.formatted(pid)).when().post("/api/admin/deals").then().statusCode(200)
                .body("status.code", is("ES-0000"));

        // Add qty=2
        given().header("X-Customer-ID", CUSTOMER_ID).contentType(ContentType.JSON).body("""
            {"productId": %d, "quantity": 2}
        """.formatted(pid)).when().post("/api/customer/basket/items")
                .then().statusCode(200).body("status.code", is("ES-0000"));

        // Receipt: original=400, discount=120, final=280
        given().header("X-Customer-ID", CUSTOMER_ID)
                .when().get("/api/customer/receipt")
                .then().statusCode(200).body("status.code", is("ES-0000"))
                .body("data.items[0].productId", is(pid.intValue()))
                .body("data.items[0].quantity", is(2))
                .body("data.items[0].unitPrice.toString()", is("200.0"))
                .body("data.items[0].originalSubtotal.toString()", is("400.0"))
                .body("data.items[0].discount.toString()", is("120.0"))
                .body("data.items[0].finalSubtotal.toString()", is("280.0"))
                .body("data.items[0].appliedDeal", is(true))
                .body("data.totalBeforeDiscount.toString()", is("400.0"))
                .body("data.totalDiscount.toString()", is("120.0"))
                .body("data.totalFinal.toString()", is("280.0"));
    }
}
