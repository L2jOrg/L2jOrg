package org.l2j.commons.database.handler;

import org.l2j.commons.database.helpers.QueryDescriptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanHandler implements TypeHandler<Boolean> {

    @Override
    public Boolean defaultValue() {
        return false;
    }

    @Override
    public Boolean handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        if(queryDescriptor.isUpdate()) {
            return queryDescriptor.getUpdateCount() > 0;
        }
        var resultSet = queryDescriptor.getResultSet();
        if(resultSet.next()) {
            return handleColumn(resultSet, 1);
        }
        return defaultValue();
    }

    @Override
    public Boolean handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        return handleColumn(resultSet, 1);
    }

    @Override
    public Boolean handleColumn(ResultSet resultSet, int column) throws SQLException {
        return resultSet.getBoolean(column);
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, Boolean arg) throws SQLException {
        statement.setBoolean(parameterIndex, arg);
    }

    @Override
    public String type() {
        return Boolean.TYPE.getName();
    }
}
