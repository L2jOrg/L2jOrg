package org.l2j.commons.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public class DatabaseFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseFactory.class);

    private static DatabaseFactory instance;
    private final HikariDataSource dataSource;

    public DatabaseFactory() throws SQLException {
        dataSource = new HikariDataSource(new HikariConfig());
        dataSource.getConnection().close();
    }

    public void shutdown() {
        try {
            dataSource.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    // TODO remove access from external module
    public static DatabaseFactory getInstance() throws SQLException {
        if (isNull(instance)) {
            instance = new DatabaseFactory();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
}