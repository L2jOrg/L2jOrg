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

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.Connection;
import java.sql.SQLException;


public class L2DatabaseFactory {
    private static Logger _log = LoggerFactory.getLogger(L2DatabaseFactory.class);

    private static L2DatabaseFactory _instance;
    private final HikariDataSource _dataSource;
    private final ApplicationContext context;

    public L2DatabaseFactory() throws SQLException {
        context = new AnnotationConfigApplicationContext(DatabaseContextConfiguration.class);
        _dataSource = context.getBean(HikariDataSource.class);

        try {
            _dataSource.getConnection().close();
        } catch (SQLException e) {
            _log.error(e.getMessage(), e);
            throw  e;
        }
    }

    public static String prepQuerySelect(String[] fields, String tableName, String whereClause, boolean returnOnlyTopRecord) {
        String msSqlTop1 = "";
        String mySqlTop1 = "";
        if (returnOnlyTopRecord) {
            mySqlTop1 = " Limit 1 ";
        }
        String query = "SELECT " + msSqlTop1 + safetyString(fields) + " FROM " + tableName + " WHERE " + whereClause + mySqlTop1;
        return query;
    }

    public void shutdown() {
        try {
            _dataSource.close();
        } catch (Exception e) {
            _log.info(e.getMessage(), e);
        }

    }

    public final static String safetyString(String[] whatToCheck) {
        // NOTE: Use brace as a safty percaution just incase name is a reserved word
        String braceLeft = "`";
        String braceRight = "`";

        String result = "";
        for (String word : whatToCheck) {
            if (result != "") result += ", ";
            result += braceLeft + word + braceRight;
        }
        return result;
    }

    // TODO remove access from external modules
    public static L2DatabaseFactory getInstance() throws SQLException {
        if (_instance == null) {
            _instance = new L2DatabaseFactory();
        }
        return _instance;
    }

    public Connection getConnection() //throws SQLException
    {
        Connection con = null;

        while (con == null) {
            try {
                con = _dataSource.getConnection();
            } catch (SQLException e) {
                _log.warn("L2DatabaseFactory: getConnection() failed, trying again", e);
            }
        }
        return con;
    }

    public static void close(Connection conn) {
    }

    public  <T> T getRepository(Class<T> repositoryClass) {
        try {
            return context.getBean(repositoryClass);
        }catch (Exception e) {
            _log.error("could.not.retrieve.repository", e);
            throw  e;
        }
    }

}