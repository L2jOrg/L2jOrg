/*
 * Copyright © 2019-2021 L2JOrg
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * @author JoeAlisson
 */
public abstract class ProvidedDAO<T> implements DAO<T> {

     protected void executeInBatch(String sql, Consumer<PreparedStatement> populateAction) throws SQLException {
        try ( var con  = DatabaseFactory.getInstance().getConnection();
            var statement = con.prepareStatement(sql)) {
            populateAction.accept(statement);
            statement.executeBatch();
        }
    }

    protected void query(String sql, Consumer<ResultSet> action) throws SQLException {
         try (var con = DatabaseFactory.getInstance().getConnection();
            var statement = con.createStatement()) {
             statement.execute(sql);
             action.accept(statement.getResultSet());
         }
    }

    protected void execute(String sql) throws SQLException {
         try (var con = DatabaseFactory.getInstance().getConnection();
              var statement = con.createStatement()) {
              statement.execute(sql);
         }
    }


}
