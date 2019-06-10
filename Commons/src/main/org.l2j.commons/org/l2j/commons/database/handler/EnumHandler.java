package org.l2j.commons.database.handler;

import org.l2j.commons.database.helpers.QueryDescriptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnumHandler implements TypeHandler<Enum<? extends  Enum>> {

    @Override
    public Enum<?> defaultValue() {
        return null;
    }

    @Override
    public Enum<?> handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        var resultSet = queryDescriptor.getResultSet();
        if(resultSet.next()) {
            return handleType(resultSet, queryDescriptor.getReturnType());
        }
        return defaultValue();
    }

    @Override
    public Enum<?> handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        return handleColumn(resultSet, 1, type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Enum<?> handleColumn(ResultSet resultSet, int column, Class type) throws SQLException {
        return Enum.valueOf(type, resultSet.getString(column));
    }

    @Override
    public Enum<?> handleColumn(ResultSet resultSet, int column) {
        throw new UnsupportedOperationException("Need to know the type of enum");
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, Enum<?> arg) throws SQLException {
        statement.setString(parameterIndex, arg.toString());
    }

    @Override
    public String type() {
        return "enum";
    }
}
