/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }
}