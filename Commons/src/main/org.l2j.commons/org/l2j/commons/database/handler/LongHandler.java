package org.l2j.commons.database.handler;

import org.l2j.commons.database.QueryDescriptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LongHandler implements  TypeHandler<Long> {
    @Override
    public Long defaultValue() {
        return 0L;
    }

    @Override
    public Long handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        if(queryDescriptor.isUpdate()) {
            return (long) queryDescriptor.getUpdateCount();
        }
        var resultSet = queryDescriptor.getResultSet();
        if(resultSet.next()) {
            return handleColumn(resultSet, 1);
        }
        return defaultValue();
    }

    @Override
    public Long handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        return handleColumn(resultSet, 1);
    }

    @Override
    public Long handleColumn(ResultSet resultSet, int column) throws SQLException {
        return resultSet.getLong(column);
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, Long arg) throws SQLException {
        statement.setLong(parameterIndex, arg);
    }

    @Override
    public String type() {
        return Long.TYPE.getName();
    }
}
