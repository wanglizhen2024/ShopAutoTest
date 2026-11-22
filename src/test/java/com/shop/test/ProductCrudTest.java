package com.shop.test;

import com.shop.api.LoginApi;
import com.shop.api.ProductApi;
import com.shop.dao.ProductDao;
import com.shop.model.LoginRequest;
import com.shop.model.ProductRequest;
import com.shop.util.DBUtil;
import com.shop.util.ExcelUtil;
import com.shop.util.GlobalVar;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

@Epic("ERP 电商系统")
@Feature("商品CRUD全流程")
public class ProductCrudTest {
    private static final Logger log = LoggerFactory.getLogger(ProductCrudTest.class);//要日志
    ProductApi api = new ProductApi();//给调用api创建对象
    ProductDao productDao = new ProductDao();

    String targetProductName;
    List<String> createdProducts = new ArrayList<>();

    @BeforeClass
    @Step("前置准备：登录获取 Token")
    public void loginFirst() {
        LoginApi loginApi = new LoginApi();
        LoginRequest loginReq = new LoginRequest("admin", "123456");
        Response response = loginApi.login(loginReq);//调用LoginApi里面的login方法并且给出要求
        response.then().statusCode(200);//判断是否成功
        GlobalVar.token = "MOCK_TOKEN_" + response.jsonPath().getString("json.username");//将token保存
        log.info("前置登录成功，拿到 Token: {}", GlobalVar.token);
    }

    @DataProvider(name = "productData")//数据驱动去读相关数据
    public Object[][] getProductData() {
        return ExcelUtil.readExcel("src/test/resources/product_data.xlsx");
    }

    @Test(priority = 1, dataProvider = "productData")
    @Story("数据驱动-新增商品并落库校验")
    @Description("从Excel读取商品数据，调用新增接口后，去数据库确认商品是否真的插入成功")
    public void testAddProduct(String name, String price, String stock) {//测试增加数据的功能
        double doublePrice = Double.parseDouble(price);
        int intStock = Integer.parseInt(stock);

        ProductRequest request = new ProductRequest(name, doublePrice, intStock);
        Response response = api.addProduct(request);
        response.then().statusCode(200);

        int dbStock = productDao.getStockByName(name);
        Assert.assertEquals(intStock, dbStock, "新增商品失败，数据库里没查到或库存不对！");
        log.info("新增商品 [{}] 成功，数据库库存为: {}", name, dbStock);

        createdProducts.add(name);
        targetProductName = name;
    }

    @Test(priority = 2, dependsOnMethods = "testAddProduct")
    @Story("修改商品价格并校验")
    public void testUpdateProduct() {
        int targetId = productDao.getIdByName(targetProductName);
        Assert.assertTrue(targetId > 0, "前置数据异常：未在数据库找到商品");

        ProductRequest updateReq = new ProductRequest(targetProductName, 199.00, 100);

        Response response = api.updateProduct(targetId, updateReq);
        response.then().statusCode(200);

        int dbStock = productDao.getStockByName(targetProductName);
        Assert.assertEquals(100, dbStock, "数据库中的库存未按预期更新！");
        log.info("修改商品 [{}] 成功，数据库库存已更新为: {}", targetProductName, dbStock);
    }

    @Test(priority = 3, dependsOnMethods = "testAddProduct")
    @Story("删除商品并确认清理")
    public void testDeleteProduct() {
        int targetId = productDao.getIdByName(targetProductName);
        Assert.assertTrue(targetId > 0, "前置数据异常：未在数据库找到商品");

        Response response = api.deleteProduct(targetId);
        response.then().statusCode(200);

        int isExist = productDao.checkExistById(targetId);
        Assert.assertEquals(-1, isExist, "【Bug】数据应该被删除了，但数据库里还能查到！");

        createdProducts.remove(targetProductName);
        log.info("删除商品 [{}] 成功，数据已从数据库彻底清理！", targetProductName);
    }

    @Test(priority = 4)
    @Story("查询商品并校验数据库库存")
    @Description("测试查询接口，并去数据库校验返回的ID是否真实存在")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetProductAndVerifyDB() {
        int testProductId = productDao.getIdByName("测试手机");
        Assert.assertTrue(testProductId > 0, "环境异常：数据库内未找到基础测试数据");

        DBUtil.update("UPDATE product SET stock = 10 WHERE id = ?", testProductId);

        Response response = api.getProduct(testProductId);
        response.then().statusCode(200);

        int responseProductId = response.jsonPath().getInt("args.productId");
        int dbStock = productDao.getStockByName("测试手机");

        Assert.assertEquals(testProductId, responseProductId, "接口返回的商品ID不对！");
        Assert.assertTrue(dbStock >= 0, "数据库里没查到该商品，数据不一致！");
        Assert.assertEquals(10, dbStock, "商品库存不对！");
    }

    @AfterClass
    public void tearDown() {
        for (String name : createdProducts) {
            int id = productDao.getIdByName(name);
            if (id > 0) {
                productDao.deleteById(id);
                log.info("【环境清理】已删除残留测试数据: {}", name);
            }
        }
    }
}