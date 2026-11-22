package com.shop.dao;

import com.shop.util.DBUtil;

public class ProductDao {
    public int getStockByName(String name) {
        String sql = "SELECT stock FROM product WHERE name = ?";
        return DBUtil.querySingleInt(sql, name);
    }//给商品名字查商品库存

    public int getIdByName(String name) {
        String sql = "SELECT id FROM product WHERE name = ?";
        return DBUtil.querySingleInt(sql, name);
    }//给商品名字给商品id

    public int deleteById(int id) {
        String sql = "DELETE FROM product WHERE id = ?";
        return DBUtil.update(sql, id);
    }//给商品id删除该商品

    public int checkExistById(int id) {
        String sql = "SELECT id FROM product WHERE id = ?";
        return DBUtil.querySingleInt(sql, id);
    }//给商品id查商品id看该商品是否存在
}