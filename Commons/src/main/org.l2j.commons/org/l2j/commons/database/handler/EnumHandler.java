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
package org.l2j.commons.database.handler;

import org.l2j.commons.database.helpers.QueryDescriptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author JoeAlisson
 */
public class EnumHandler implements TypeHandler<Enum<?>> {

    @Override
    public Enum<?> defaultValue() {
        return null;
    }

    @Override
    public Enum<?> handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        var resultSet = queryDescriptor.getResultSet();
        if(resultSet.next()) {
            return handleType(resultSet, queryDescriptor.getReturnType());
        }
        return defaultValue();
    }

    @Override
    public Enum<?> handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        return handleColumn(resultSet, 1, type);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Enum<?> handleColumn(ResultSet resultSet, int column, Class type) throws SQLException {
        return Enum.valueOf(type, resultSet.getString(column));
    }

    @Override
    public Enum<?> handleColumn(ResultSet resultSet, int column) {
        throw new UnsupportedOperationException("Need to know the type of enum");
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, Enum<?> arg) throws SQLException {
        statement.setString(parameterIndex, arg.toString());
    }

    @Override
    public String type() {
        return "enum";
    }
}
