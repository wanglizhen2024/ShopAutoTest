package com.shop.util;

import com.shop.config.ConfigUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;

public class DBUtil {
    private static final Logger log = LoggerFactory.getLogger(DBUtil.class);
    private static HikariDataSource dataSource;
    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(ConfigUtil.getValue("db.url"));
            config.setUsername(ConfigUtil.getValue("db.user"));
            config.setPassword(ConfigUtil.getValue("db.password"));
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            log.error("数据库连接池初始化失败！", e);
            throw new RuntimeException("数据库连接池初始化失败！", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static int querySingleInt(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            log.error("数据库查询异常！SQL: " + sql, e);
        }
        return -1;
    }

    public static int update(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("数据库更新异常！SQL: " + sql, e);
            return 0;
        }
    }
}
