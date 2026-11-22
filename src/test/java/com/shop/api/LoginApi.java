package com.shop.api;

import com.shop.model.LoginRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class LoginApi {

    @Step("调用【登录】接口，获取 Token")
    public Response login(LoginRequest request) {
        return given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("https://httpbin.org/post") // 模拟真实登录接口
                .then()
                .extract().response();
    }
}