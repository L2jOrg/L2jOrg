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
package org.l2j.commons.database.handler;

import org.l2j.commons.database.QueryDescriptor;
import org.l2j.commons.database.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShortHandler implements TypeHandler<Short> {

    @Override
    public Short defaultValue() {
        return 0;
    }

    @Override
    public Short handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        if(queryDescriptor.isUpdate()) {
            return queryDescriptor.getUpdateCount().shortValue();
        }
        var resultSet = queryDescriptor.getResultSet();
        if(resultSet.next()) {
            return handleColumn(resultSet, 1);
        }
        return defaultValue();
    }

    @Override
    public Short handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        return handleColumn(resultSet, 1);
    }

    @Override
    public Short handleColumn(ResultSet resultSet, int column) throws SQLException {
        return resultSet.getShort(column);
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, Short arg) throws SQLException {
        statement.setShort(parameterIndex, arg);
    }

    @Override
    public String type() {
        return Short.TYPE.getName() ;
    }
}
