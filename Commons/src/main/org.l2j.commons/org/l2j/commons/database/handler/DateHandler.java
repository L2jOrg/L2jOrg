package org.l2j.commons.database.handler;

import org.l2j.commons.database.helpers.QueryDescriptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class DateHandler implements TypeHandler<Date> {

    @Override
    public Date defaultValue() {
        return new Date();
    }

    @Override
    public Date handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        var resultSet = queryDescriptor.getResultSet();
        if(resultSet.next()) {
            return handleColumn(resultSet, 1);
        }
        return defaultValue();
    }

    @Override
    public Date handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        return handleColumn(resultSet, 1);
    }

    @Override
    public Date handleColumn(ResultSet resultSet, int column) throws SQLException {
        return resultSet.getDate(column);
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, Date arg) throws SQLException {
        statement.setDate(parameterIndex, new java.sql.Date(arg.getTime()));
    }

    @Override
    public String type() {
        return Date.class.getName();
    }
}
