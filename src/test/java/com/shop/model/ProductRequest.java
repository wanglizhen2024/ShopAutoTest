package com.shop.model;

public class ProductRequest {
    private String name;
    private double price;
    private int stock;

    public ProductRequest(String name, double price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
    public ProductRequest(){

    }

    // 生成 Getter 和 Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}