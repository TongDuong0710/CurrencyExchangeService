package com.example.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * End-to-end happy flow test:
 * 1. Admin creates a product (iPhone 15).
 * 2. Admin sets a "Buy 1, get 2nd at 50% off" deal with future expiry.
 * 3. Customer queries catalog and sees the product available.
 * 4. Customer adds 2 units to basket:
 *    - Stock decreases from 10 → 8.
 *    - Receipt applies the deal correctly: unit=1500, subtotal=3000, discount=750, final=2250.
 *
 * This verifies the integration of product creation, deal setup, catalog filtering,
 * basket management, stock update, and receipt calculation.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CheckoutHappyFlowTests extends BaseE2E {
    /*
     End-to-end test scenario:
     1. Admin creates a product (iPhone 15).
     2. Admin creates a "buy 1 get 2nd 50% off" deal for that product.
     3. Customer queries catalog with filters and sees the product available.
     4. Customer adds 2 units to basket:
          - Stock decrements from 10 → 8.
          - Receipt applies the deal correctly:
              unitPrice=1500, subtotal=3000, discount=750, final=2250.
     This flow validates both admin and customer sides, including catalog, basket, stock, and receipt.
    */

    // customer id across basket/receipt calls
    private static final String CUSTOMER_ID = "test-user-1";

    static Long productId;

    @Test @Order(1)
    void admin_create_product() {
        productId =
                given()
                        .contentType(ContentType.JSON)
                        .body("""
                      {
                        "name":"iPhone 15",
                        "description":"A17",
                        "category":"PHONE",
                        "price":1500.00,
                        "stock":10
                      }
                      """)
                        .when()
                        .post("/api/admin/products")
                        .then()
                        .statusCode(200)
                        .body("data.id", notNullValue())
                        .body("data.name", is("iPhone 15"))
                        .extract()
                        .jsonPath().getLong("data.id");
    }

    @Test @Order(2)
    void admin_create_deal_buy1_second_half_with_future_expiry() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                  {
                    "productId": %d,
                    "dealType": "B1G50_2ND",
                    "expirationDateTime": "2030-12-31T23:59:59"
                  }
                  """.formatted(productId))
                .when()
                .post("/api/admin/deals")
                .then()
                .statusCode(200)
                .body("status.code", is("ES-0000"))
                .body("data.productId", is(productId.intValue()))
                .body("data.dealType", is("B1G50_2ND"));
    }

    @Test @Order(3)
    void customer_catalog_filter_and_pagination() {
        given()
                .queryParam("page", 0)
                .queryParam("size", 5)
                .queryParam("category", "PHONE")
                .queryParam("minPrice", 1000)
                .queryParam("maxPrice", 2000)
                .queryParam("available", true)
                .when()
                .get("/api/customer/products")
                .then()
                .statusCode(200)
                .body("status.code", is("ES-0000"))
                .body("status.message", is("Success"))
                .body("data.number", is(0))
                .body("data.size", is(5))
                .body("data.totalElements", greaterThanOrEqualTo(1))
                .body("data.content", notNullValue())
                .body("data.content[0].id", notNullValue())
                .body("data.content[0].name", is("iPhone 15"))
                .body("data.content[0].category", is("PHONE"))
                .body("data.content[0].price", is(1500.00F))
                .body("data.content[0].available", is(true));
    }

    @Test
    @Order(4)
    void add_to_basket_should_decrement_stock_and_apply_deal_in_receipt() {
        // Add to basket
        given()
                .header("X-Customer-ID", CUSTOMER_ID)
                .contentType(ContentType.JSON)
                .body("""
              {"productId": %d, "quantity": 2}
              """.formatted(productId))
                .when()
                .post("/api/customer/basket/items")
                .then()
                .statusCode(200)
                .body("status.code", is("ES-0000"));

        // Stock decreased
        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("category", "PHONE")
                .when()
                .get("/api/admin/products")
                .then()
                .statusCode(200)
                .body("status.code", is("ES-0000"))
                .body("data.content.find { it.id == %d }.stock".formatted(productId), is(8));

        // Receipt check (expect: unit=1500, subtotal=1500*2=3000, discount=750, final=2250)
        given()
                .header("X-Customer-ID", CUSTOMER_ID)
                .when()
                .get("/api/customer/receipt")
                .then()
                .statusCode(200)
                .body("status.code", is("ES-0000"))
                .body("data.items[0].productId", is(productId.intValue()))
                .body("data.items[0].productName", is("iPhone 15"))
                .body("data.items[0].quantity", is(2))
                .body("data.items[0].unitPrice.toString()", is("1500.0"))     // unitPrice = 1500
                .body("data.items[0].originalSubtotal.toString()", is("3000.0")) // 1500*2
                .body("data.items[0].discount.toString()", is("750.0"))          // 50% of one unit
                .body("data.items[0].finalSubtotal.toString()", is("2250.0"))    // 3000-750
                .body("data.items[0].appliedDeal", is(true))
                .body("data.totalBeforeDiscount.toString()", is("3000.0"))
                .body("data.totalDiscount.toString()", is("750.0"))
                .body("data.totalFinal.toString()", is("2250.0"));
    }
}
