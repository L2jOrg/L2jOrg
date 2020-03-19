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
