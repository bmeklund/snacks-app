package com.demo;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(SnackResource.class)
class SnackResourceTest {

    @Test
    void listsSeededSnacksSortedByName() {
        List<String> names = when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("name", String.class);

        assertTrue(names.containsAll(List.of("Beer", "Chips", "Pepsi", "Pizza")));

        List<String> sortedNames = new ArrayList<>(names);
        sortedNames.sort(String::compareTo);
        assertEquals(sortedNames, names);
    }

    @Test
    void createsUpdatesReadsAndDeletesSnack() {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        String originalName = "Test snack " + uniqueSuffix;
        String updatedName = "Updated snack " + uniqueSuffix;
        Long snackId = null;

        try {
            snackId = given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("name", originalName))
                    .when()
                    .post()
                    .then()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("name", equalTo(originalName))
                    .extract()
                    .jsonPath()
                    .getLong("id");

            given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("name", updatedName))
                    .pathParam("id", snackId)
                    .when()
                    .put("/{id}")
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(snackId.intValue()))
                    .body("name", equalTo(updatedName));

            given()
                    .pathParam("id", snackId)
                    .when()
                    .get("/{id}")
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(snackId.intValue()))
                    .body("name", equalTo(updatedName));

            given()
                    .pathParam("id", snackId)
                    .when()
                    .delete("/{id}")
                    .then()
                    .statusCode(204);

            given()
                    .pathParam("id", snackId)
                    .when()
                    .get("/{id}")
                    .then()
                    .statusCode(404);

            snackId = null;
        } finally {
            if (snackId != null) {
                given()
                        .pathParam("id", snackId)
                        .when()
                        .delete("/{id}");
            }
        }
    }

    @Test
    void rejectsInvalidCreateAndUpdateRequests() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("id", 99, "name", "Invalid snack"))
                .when()
                .post()
                .then()
                .statusCode(422);

        given()
                .contentType(ContentType.JSON)
                .body(Map.of())
                .pathParam("id", 1)
                .when()
                .put("/{id}")
                .then()
                .statusCode(422);
    }

    @Test
    void returnsConflictWhenCreatingDuplicateSnack() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("name", "Pizza"))
                .when()
                .post()
                .then()
                .statusCode(409)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Snack with name 'Pizza' already exists."));
    }

    @Test
    void returnsConflictWhenUpdatingToDuplicateSnack() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("name", "Pizza"))
                .pathParam("id", 2)
                .when()
                .put("/{id}")
                .then()
                .statusCode(409)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Snack with name 'Pizza' already exists."));

        given()
                .pathParam("id", 2)
                .when()
                .get("/{id}")
                .then()
                .statusCode(200)
                .body("name", equalTo("Beer"));
    }

    @Test
    void returnsNotFoundForUnknownSnack() {
        given()
                .pathParam("id", Long.MAX_VALUE)
                .when()
                .get("/{id}")
                .then()
                .statusCode(404);

        given()
                .pathParam("id", Long.MAX_VALUE)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(404);
    }
}
