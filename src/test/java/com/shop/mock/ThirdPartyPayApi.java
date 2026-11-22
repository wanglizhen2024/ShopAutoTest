package com.shop.mock;

public interface ThirdPartyPayApi {
    boolean pay(String orderId, double amount);
}