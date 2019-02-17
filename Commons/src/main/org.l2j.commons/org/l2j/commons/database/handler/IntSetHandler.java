package org.l2j.commons.database.handler;


import io.github.joealisson.primitive.sets.IntSet;
import io.github.joealisson.primitive.sets.impl.HashIntSet;
import org.l2j.commons.database.QueryDescriptor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IntSetHandler implements TypeHandler<IntSet> {

    @Override
    public IntSet defaultValue() {
        return new HashIntSet();
    }

    @Override
    public IntSet handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        IntSet result = new HashIntSet();
        ResultSet resultSet = queryDescriptor.getStatement().getResultSet();
        while (resultSet.next()) {
            result.add(resultSet.getInt(1));
        }
        return result;
    }

    @Override
    public IntSet handle(ResultSet resultSet, Class<?> type) {
        return new HashIntSet();
    }

    @Override
    public String type() {
        return IntSet.class.getName();
    }
}
