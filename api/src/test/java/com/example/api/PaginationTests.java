package com.example.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class PaginationTests extends BaseE2E {

    /**
     * Scenario: Verify pagination on product listing.
     * - Seed 7 products.
     * - Request page=1, size=5.
     * - Expect:
     *   page number = 1, page size = 5,
     *   returned items <= 5,
     *   totalElements >= 7.
     */
    @Test
    void admin_products_pagination_works() {
        for (int i = 0; i < 7; i++) {
            given()
                    .contentType(ContentType.JSON)
                    .body("""
                      {"name":"Prod-%d","description":"d","category":"ACC","price":10,"stock":5}
                      """.formatted(i))
                    .when().post("/api/admin/products")
                    .then().statusCode(200);
        }

        given()
                .queryParam("page", 1)
                .queryParam("size", 5)
                .when().get("/api/admin/products")
                .then()
                .statusCode(200)
                .body("status.code", is("ES-0000"))
                .body("data.number", is(1))
                .body("data.size", is(5))
                .body("data.content.size()", lessThanOrEqualTo(5))
                .body("data.totalElements", greaterThanOrEqualTo(7));
    }

    /**
     * Scenario: Verify pagination on deals listing.
     * - Seed 6 products and create one deal per product (future expiry).
     * - Request page=0, size=5.
     * - Expect:
     *   page number = 0, page size = 5,
     *   returned items = 5,
     *   totalElements >= 6.
     */
    @Test
    void admin_deals_pagination_works() {
        for (int i = 0; i < 6; i++) {
            Long pId =
                    given()
                            .contentType(ContentType.JSON)
                            .body("""
                          {"name":"Seed-%d","description":"d","category":"ACC","price":1,"stock":50}
                          """.formatted(i))
                            .when().post("/api/admin/products")
                            .then().statusCode(200)
                            .extract().jsonPath().getLong("data.id");

            given()
                    .contentType(ContentType.JSON)
                    .body("""
                      {
                        "productId": %d,
                        "dealType": "B1G50_2ND",
                        "expirationDateTime": "2099-12-31T00:00:00"
                      }
                      """.formatted(pId))
                    .when().post("/api/admin/deals")
                    .then()
                    .statusCode(200)
                    .body("status.code", is("ES-0000"));
        }

        given()
                .queryParam("page", 0)
                .queryParam("size", 5)
                .when().get("/api/admin/deals")
                .then()
                .statusCode(200)
                .body("status.code", is("ES-0000"))
                .body("data.number", is(0))
                .body("data.size", is(5))
                .body("data.content.size()", is(5))
                .body("data.totalElements", greaterThanOrEqualTo(6));
    }
}
