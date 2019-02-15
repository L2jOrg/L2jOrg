package org.l2j.commons.database.handler;

import org.l2j.commons.database.QueryDescriptor;

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
            return queryDescriptor.getStatement().getUpdateCount();
        }
        var resultSet = queryDescriptor.getStatement().getResultSet();
        if(resultSet.next()) {
            return handle(resultSet, null);
        }
        return 0;
    }

    @Override
    public Integer handle(ResultSet resultSet, Class<?> type) throws SQLException {
        return resultSet.getInt(1);
    }

    @Override
    public String type() {
        return Integer.TYPE.getName() ;
    }
}
