package org.l2j.commons.database.handler;

import org.l2j.commons.database.helpers.QueryDescriptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IntegerHandler implements TypeHandler<Integer> {

    @Override
    public Integer defaultValue() {
        return 0;
    }

    @Override
    public Integer handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        if(queryDescriptor.isUpdate()) {
            return queryDescriptor.getUpdateCount();
        }
        var resultSet = queryDescriptor.getResultSet();
        if(resultSet.next()) {
            return handleColumn(resultSet, 1);
        }
        return defaultValue();
    }

    @Override
    public Integer handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        return handleColumn(resultSet, 1);
    }

    @Override
    public Integer handleColumn(ResultSet resultSet, int column) throws SQLException {
        return resultSet.getInt(column);
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, Integer arg) throws SQLException {
        statement.setInt(parameterIndex, arg);
    }

    @Override
    public String type() {
        return Integer.TYPE.getName() ;
    }
}
