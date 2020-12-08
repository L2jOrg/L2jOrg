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

import io.github.joealisson.primitive.IntKeyIntValue;
import org.l2j.commons.database.QueryDescriptor;
import org.l2j.commons.database.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author JoeAlisson
 */
public class IntKeyIntValueHandler implements TypeHandler<IntKeyIntValue> {
    @Override
    public IntKeyIntValue defaultValue() {
        return null;
    }

    @Override
    public IntKeyIntValue handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        var resultSet = queryDescriptor.getResultSet();
        if(resultSet.next()) {
            return handleColumn(resultSet, 1);
        }
        return defaultValue();
    }

    @Override
    public IntKeyIntValue handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        return handleColumn(resultSet, 1);
    }

    @Override
    public IntKeyIntValue handleColumn(ResultSet resultSet, int column) throws SQLException {
        return new IntKeyIntValue(resultSet.getInt(column), resultSet.getInt(column+1));
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, IntKeyIntValue arg) {

    }

    @Override
    public String type() {
        return IntKeyIntValue.class.getName();
    }
}
