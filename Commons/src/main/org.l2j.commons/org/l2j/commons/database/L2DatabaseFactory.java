/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package org.l2j.commons.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

import static java.util.Objects.isNull;

public class L2DatabaseFactory {
    private static Logger LOGGER = LoggerFactory.getLogger(L2DatabaseFactory.class);

    private static L2DatabaseFactory instance;
    private final HikariDataSource _dataSource;

    public L2DatabaseFactory() throws SQLException {
        _dataSource = new HikariDataSource(new HikariConfig());
        _dataSource.getConnection().close();
    }

    public void shutdown() {
        try {
            _dataSource.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }


    // TODO remove access from external modules
    public static L2DatabaseFactory getInstance() throws SQLException {
        if (isNull(instance)) {
            instance = new L2DatabaseFactory();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            return _dataSource.getConnection();
        } catch (SQLException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public  <T> T getRepository(Class<T> repositoryClass) {
        try {
            return null;
        }catch (Exception e) {
            LOGGER.error("could.not.retrieve.repository", e);
            throw  e;
        }
    }

}