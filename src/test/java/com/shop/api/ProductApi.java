package com.shop.api;

import com.shop.model.ProductRequest;
import com.shop.util.GlobalVar;
import io.qameta.allure.Step;
import io.restassured.response.Response;


import static io.restassured.RestAssured.given;

public class ProductApi {

    public Response getProduct(int productId) {
        return given()
                .header("Authorization", "Bearer " + GlobalVar.token)
                .queryParam("productId", productId)
                .when()
                .get("https://httpbin.org/get")
                .then()
                .extract().response();
    }

    public Response addProduct(ProductRequest request) {
        return given()
                .header("Authorization", "Bearer " + GlobalVar.token)
                .contentType("application/json")
                .body(request)
                .when()
                .post("https://httpbin.org/post")
                .then()
                .extract().response();
    }

    public Response updateProduct(int productId, ProductRequest request) {
        return given()
                .header("Authorization", "Bearer " + GlobalVar.token)
                .contentType("application/json")
                .body(request)
                .when()
                .put("https://httpbin.org/put?productId=" + productId)
                .then()
                .extract().response();
    }

    public Response deleteProduct(int productId) {
        return given()
                .header("Authorization", "Bearer " + GlobalVar.token)
                .when()
                .delete("https://httpbin.org/delete?productId=" + productId)
                .then()
                .extract().response();
    }
}