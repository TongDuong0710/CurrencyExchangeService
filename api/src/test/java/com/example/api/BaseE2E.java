package com.example.api;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseE2E {

    @LocalServerPort
    int port;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeAll
    static void setupRestAssured(@LocalServerPort int port) {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @AfterAll
    static void cleanDatabase(@Autowired DataSource dataSource,
                              @Autowired JdbcTemplate jdbc) throws Exception {
        try (var conn = dataSource.getConnection()) {
            String db = conn.getMetaData().getDatabaseProductName().toLowerCase();
            if (db.contains("h2")) {
                jdbc.execute("SET REFERENTIAL_INTEGRITY FALSE");
                var rs = conn.createStatement().executeQuery(
                        "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'"
                );
                while (rs.next()) {
                    jdbc.execute("TRUNCATE TABLE " + rs.getString(1));
                }
                jdbc.execute("SET REFERENTIAL_INTEGRITY TRUE");
            } else if (db.contains("postgresql")) {
                var rs = conn.createStatement().executeQuery(
                        "SELECT tablename FROM pg_tables WHERE schemaname = 'public'"
                );
                StringBuilder sb = new StringBuilder();
                while (rs.next()) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(rs.getString(1));
                }
                if (sb.length() > 0) {
                    jdbc.execute("TRUNCATE " + sb + " RESTART IDENTITY CASCADE");
                }
            }
        }
    }
}
