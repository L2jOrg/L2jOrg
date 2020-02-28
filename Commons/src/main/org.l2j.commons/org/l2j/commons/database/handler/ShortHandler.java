package org.l2j.commons.database.handler;

import org.l2j.commons.database.helpers.QueryDescriptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShortHandler implements TypeHandler<Short> {

    @Override
    public Short defaultValue() {
        return 0;
    }

    @Override
    public Short handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        if(queryDescriptor.isUpdate()) {
            return queryDescriptor.getUpdateCount().shortValue();
        }
        var resultSet = queryDescriptor.getResultSet();
        if(resultSet.next()) {
            return handleColumn(resultSet, 1);
        }
        return defaultValue();
    }

    @Override
    public Short handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        return handleColumn(resultSet, 1);
    }

    @Override
    public Short handleColumn(ResultSet resultSet, int column) throws SQLException {
        return resultSet.getShort(column);
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, Short arg) throws SQLException {
        statement.setShort(parameterIndex, arg);
    }

    @Override
    public String type() {
        return Short.TYPE.getName() ;
    }
}
