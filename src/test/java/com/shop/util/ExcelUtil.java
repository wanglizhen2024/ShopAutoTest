package com.shop.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelUtil {
    public static Object[][] readExcel(String filePath) {
        List<Map<Integer, String>> list = new ArrayList<>();
        EasyExcel.read(filePath, new ReadListener<Map<Integer, String>>() {
            @Override
            public void invoke(Map<Integer, String> data, AnalysisContext context) {
                if (context.readRowHolder().getRowIndex() != 0) {
                    list.add(data);
                }
            }
            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {}
        }).sheet().doRead();

        Object[][] result = new Object[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            Map<Integer, String> row = list.get(i);
            result[i] = new Object[]{
                    row.get(0) != null ? row.get(0).trim() : "",
                    row.get(1) != null ? row.get(1).trim() : "",
                    row.get(2) != null ? row.get(2).trim() : ""
            };
        }
        return result;
    }
}