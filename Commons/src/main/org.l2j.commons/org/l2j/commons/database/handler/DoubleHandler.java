package org.l2j.commons.database.handler;

import org.l2j.commons.database.helpers.QueryDescriptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoubleHandler implements TypeHandler<Double> {

    @Override
    public Double defaultValue() {
        return .0;
    }

    @Override
    public Double handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        if(queryDescriptor.isUpdate()) {
            return Double.valueOf(queryDescriptor.getUpdateCount());
        }
        var resultSet = queryDescriptor.getResultSet();
        if(resultSet.next()) {
            return handleColumn(resultSet, 1);
        }
        return defaultValue();
    }

    @Override
    public Double handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        return handleColumn(resultSet, 1);
    }

    @Override
    public Double handleColumn(ResultSet resultSet, int column) throws SQLException {
        return resultSet.getDouble(column);
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, Double arg) throws SQLException {
        statement.setDouble(parameterIndex, arg);
    }

    @Override
    public String type() {
        return Double.TYPE.getName();
    }
}
