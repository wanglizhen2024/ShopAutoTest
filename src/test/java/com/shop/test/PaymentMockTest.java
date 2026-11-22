package com.shop.test;

import com.shop.mock.PaymentService;
import com.shop.mock.ThirdPartyPayApi;
import io.qameta.allure.*;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
@Epic("ERP 电商系统")
@Feature("支付模块")
public class PaymentMockTest {
    private static final Logger log = LoggerFactory.getLogger(PaymentMockTest.class);

    @Test(enabled = false)
    @Story("模拟第三方支付成功")
    @Description("隔离真实微信支付，使用Mock测试支付成功的业务逻辑")
    public void testPaymentSuccess() {
        log.info("开始执行支付Mock测试");
        ThirdPartyPayApi mockApi = mock(ThirdPartyPayApi.class);
        when(mockApi.pay(anyString(), anyDouble())).thenReturn(true);

        PaymentService service = new PaymentService(mockApi);
        String result = service.processPayment("ORDER_001", 100.0);

        Assert.assertEquals(result, "支付成功");
        verify(mockApi, times(1)).pay("ORDER_001", 100.0);
    }
}