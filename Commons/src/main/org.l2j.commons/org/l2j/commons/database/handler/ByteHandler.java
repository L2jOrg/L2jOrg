package org.l2j.commons.database.handler;

import org.l2j.commons.database.helpers.QueryDescriptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ByteHandler implements TypeHandler<Byte> {

    @Override
    public Byte defaultValue() {
        return 0;
    }

    @Override
    public Byte handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        if(queryDescriptor.isUpdate()) {
            return queryDescriptor.getUpdateCount().byteValue();
        }
        var resultSet = queryDescriptor.getResultSet();
        if(resultSet.next()) {
            return handleColumn(resultSet, 1);
        }
        return defaultValue();
    }

    @Override
    public Byte handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        return handleColumn(resultSet, 1);
    }

    @Override
    public Byte handleColumn(ResultSet resultSet, int column) throws SQLException {
        return resultSet.getByte(column);
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, Byte arg) throws SQLException {
        statement.setByte(parameterIndex, arg);
    }

    @Override
    public String type() {
        return Byte.TYPE.getName();
    }
}
