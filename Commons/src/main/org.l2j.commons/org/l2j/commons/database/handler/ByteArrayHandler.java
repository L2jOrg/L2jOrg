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
import org.l2j.commons.util.Util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author JoeAlisson
 */
public class ByteArrayHandler implements TypeHandler<byte[]> {

    @Override
    public byte[] defaultValue() {
        return Util.BYTE_ARRAY_EMPTY;
    }

    @Override
    public byte[] handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        var resultSet = queryDescriptor.getResultSet();
        if(resultSet.next()) {
            return handleColumn(resultSet, 1);
        }
        return defaultValue();
    }

    @Override
    public byte[] handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        return handleColumn(resultSet, 1);
    }

    @Override
    public byte[] handleColumn(ResultSet resultSet, int column) throws SQLException {
        return resultSet.getBytes(column);
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, byte[] arg) throws SQLException {
        statement.setBytes(parameterIndex, arg);
    }

    @Override
    public String type() {
        return byte[].class.getName();
    }
}
