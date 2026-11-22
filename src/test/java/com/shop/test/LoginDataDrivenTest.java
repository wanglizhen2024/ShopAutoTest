package com.shop.test;

import com.shop.api.LoginApi;
import com.shop.model.LoginRequest;
import com.shop.util.ExcelUtil;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Epic("ERP 电商系统")
@Feature("登录模块")
public class LoginDataDrivenTest {
    private static final Logger log = LoggerFactory.getLogger(LoginDataDrivenTest.class);
    LoginApi loginApi = new LoginApi();

    @DataProvider(name = "loginData")
    public Object[][] getData() {
        return ExcelUtil.readExcel("src/test/resources/login_data.xlsx");
    }

    @Test(dataProvider = "loginData")
    @Story("数据驱动登录测试")
    public void testLoginWithExcel(String username, String password, String expectedResult) {
        log.info("执行用例：账号 [" + username + "]，预期 [" + expectedResult + "]");
        LoginRequest req = new LoginRequest(username, password);
        Response response = loginApi.login(req);

        response.then().statusCode(200);

        String responseUsername = response.jsonPath().getString("json.username");

        Assert.assertEquals(responseUsername, username, "返回的账号和传入的不一致");
    }
}