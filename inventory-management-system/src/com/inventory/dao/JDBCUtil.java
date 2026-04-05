package com.inventory.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBCUtil {
    private static final Logger LOGGER = Logger.getLogger(JDBCUtil.class.getName());
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/inventory_db";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        String url = getConfig("inventory.db.url", "DB_URL", DEFAULT_URL);
        String user = getConfig("inventory.db.user", "DB_USER", DEFAULT_USER);
        String password = getConfig("inventory.db.password", "DB_PASSWORD", DEFAULT_PASSWORD);

        loadDriverForUrl(url);

        if (user == null || user.trim().isEmpty()) {
            throw new SQLException("Database user is not configured. Set inventory.db.user or DB_USER.");
        }

        LOGGER.log(Level.FINE, "Opening DB connection to {0}", url);
        return DriverManager.getConnection(url, user, password);
    }

    private static void loadDriverForUrl(String url) {
        try {
            if (url.startsWith("jdbc:h2:")) {
                Class.forName("org.h2.Driver");
                return;
            }
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("JDBC driver not found for URL: " + url, e);
        }
    }

    private static String getConfig(String systemProperty, String envVar, String defaultValue) {
        String value = System.getProperty(systemProperty);
        if (value != null && !value.trim().isEmpty()) {
            return value.trim();
        }

        value = System.getenv(envVar);
        if (value != null && !value.trim().isEmpty()) {
            return value.trim();
        }

        return defaultValue;
    }
}
