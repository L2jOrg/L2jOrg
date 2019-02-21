package org.l2j.commons.database.handler;

import org.l2j.commons.database.QueryDescriptor;
import org.l2j.commons.util.Util;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StringHandler implements TypeHandler<String> {

    @Override
    public String defaultValue() {
        return Util.STRING_EMPTY;
    }

    @Override
    public String handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        var resultSet = queryDescriptor.getStatement().getResultSet();
        if(resultSet.next()) {
            return handle(resultSet, null);
        }
        return Util.STRING_EMPTY;
    }

    @Override
    public String handle(ResultSet resultSet, Class<?> type) throws SQLException {
        return resultSet.getString(1);
    }

    @Override
    public String type() {
        return String.class.getName();
    }
}
