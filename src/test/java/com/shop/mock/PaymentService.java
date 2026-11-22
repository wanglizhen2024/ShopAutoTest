package com.shop.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private ThirdPartyPayApi api;

    public PaymentService(ThirdPartyPayApi api) {
        this.api = api;
    }

    public String processPayment(String orderId, double amount) {
        if (amount <= 0) {
            return "金额非法";
        }
        boolean isSuccess = api.pay(orderId, amount);
        if (isSuccess) {
            log.info("订单 " + orderId + " 支付成功！");
            return "支付成功";
        } else {
            log.error("订单 " + orderId + " 支付失败！");
            return "支付失败";
        }
    }
}