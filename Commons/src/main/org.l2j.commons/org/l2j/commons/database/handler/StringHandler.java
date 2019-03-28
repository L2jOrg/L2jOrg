package org.l2j.commons.database.handler;

import org.l2j.commons.database.QueryDescriptor;
import org.l2j.commons.util.Util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StringHandler implements TypeHandler<String> {

    @Override
    public String defaultValue() {
        return Util.STRING_EMPTY;
    }

    @Override
    public String handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        var resultSet = queryDescriptor.getResultSet();
        if(resultSet.next()) {
            return handleColumn(resultSet, 1);
        }
        return defaultValue();
    }

    @Override
    public String handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        return handleColumn(resultSet, 1);
    }

    @Override
    public String handleColumn(ResultSet resultSet, int column) throws SQLException {
        return resultSet.getString(column);
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, String arg) throws SQLException {
        statement.setString(parameterIndex, arg);
    }

    @Override
    public String type() {
        return String.class.getName();
    }
}
