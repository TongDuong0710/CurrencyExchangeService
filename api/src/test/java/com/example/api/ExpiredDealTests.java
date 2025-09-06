package com.example.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

/**
 * Scenario: Expired deal should not be applied
 *
 * Steps:
 * 1. Create a product with price = 1000.
 * 2. Create a B1G50_2ND deal but already expired.
 * 3. Add 2 items to the basket.
 * 4. Check receipt:
 *    - No deal applied (appliedDeal = false).
 *    - Subtotal = 2 * 1000 = 2000, discount = 0, final = 2000.
 *
 * Purpose: Ensure expired deals do not affect receipt calculation.
 */
class ExpiredDealTests extends BaseE2E {

    // customer id
    private static final String CUSTOMER_ID = "test-user-expired";

    @Test
    void expired_deal_should_not_be_applied() {
        // 1) Create product
        Long pId =
                given()
                        .contentType(ContentType.JSON)
                        .body("""
                          {
                            "name":"Promo",
                            "description":"x",
                            "category":"PHONE",
                            "price":1000.00,
                            "stock":10
                          }
                        """)
                        .when().post("/api/admin/products")
                        .then()
                        .statusCode(200)
                        .body("status.code", is("ES-0000"))
                        .extract()
                        .jsonPath().getLong("data.id"); // ensure Long

        // 2) Create EXPIRED deal (must not apply)
        given()
                .contentType(ContentType.JSON)
                .body("""
                  {
                    "productId": %d,
                    "dealType": "B1G50_2ND",
                    "expirationDateTime": "2015-12-31T23:59:59"
                  }
                """.formatted(pId))
                .when().post("/api/admin/deals")
                .then()
                .statusCode(200)
                .body("status.code", is("ES-0000"));

        // 3) Add 2 items
        given()
                .header("X-Customer-ID", CUSTOMER_ID)
                .contentType(ContentType.JSON)
                .body("""
                  {"productId": %d, "quantity": 2}
                """.formatted(pId))
                .when().post("/api/customer/basket/items")
                .then()
                .statusCode(200)
                .body("status.code", is("ES-0000"));

        // 4) Receipt: expired => no discount
        // Math: unit=1000, qty=2 -> original=2000, discount=0, final=2000
        given()
                .header("X-Customer-ID", CUSTOMER_ID)
                .when().get("/api/customer/receipt")
                .then()
                .statusCode(200)
                .body("status.code", is("ES-0000"))
                .body("data.items[0].productId", is(pId.intValue()))
                .body("data.items[0].quantity", is(2))
                .body("data.items[0].unitPrice.toString()", is("1000.0"))
                .body("data.items[0].originalSubtotal.toString()", is("2000.0"))
                .body("data.items[0].discount.toString()", is("0.0"))
                .body("data.items[0].finalSubtotal.toString()", is("2000.0"))
                .body("data.items[0].appliedDeal", is(false))
                .body("data.totalBeforeDiscount.toString()", is("2000.0"))
                .body("data.totalDiscount.toString()", is("0.0"))
                .body("data.totalFinal.toString()", is("2000.0"));
    }
}
