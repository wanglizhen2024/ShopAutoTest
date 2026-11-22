package com.shop.test;

import com.shop.dao.ProductDao;
import com.shop.util.DBUtil;
import io.qameta.allure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.concurrent.*;

@Epic("ERP 电商系统")
@Feature("商品并发模块")
public class ConcurrentOrderTest {
    private static final Logger log = LoggerFactory.getLogger(ConcurrentOrderTest.class);
    ProductDao productDao = new ProductDao();

    @Test
    @Story("并发扣减库存防超卖")
    public void testConcurrentDeductStock() throws InterruptedException {
        DBUtil.update("UPDATE product SET stock = 5 WHERE id = 1");
        log.info("前置准备：库存重置为 5");

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGun = new CountDownLatch(1);
        CountDownLatch finishLine = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    startGun.await();
                    String sql = "UPDATE product SET stock = stock - 1 WHERE id = 1 AND stock > 0";
                    int rows = DBUtil.update(sql);
                    if (rows > 0) {
                        log.info(Thread.currentThread().getName() + " 抢购成功");
                    } else {
                        log.warn(Thread.currentThread().getName() + " 抢购失败，库存不足");
                    }
                } catch (Exception e) {
                    log.error("并发执行异常", e);
                } finally {
                    finishLine.countDown();
                }
            });
        }

        startGun.countDown();
        finishLine.await();
        executor.shutdown();

        int finalStock = productDao.getStockByName("测试手机");
        log.info("并发结束，数据库最终库存: " + finalStock);

        Assert.assertTrue(finalStock >= 0, "库存超卖！");
        Assert.assertEquals(finalStock, 0, "库存未按预期扣减为0！");
    }
}