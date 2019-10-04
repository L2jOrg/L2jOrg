package org.l2j.commons.database.handler;

import io.github.joealisson.primitive.IntSet;
import io.github.joealisson.primitive.HashIntSet;
import org.l2j.commons.database.helpers.QueryDescriptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author JoeAlisson
 */
public class IntSetHandler implements TypeHandler<IntSet> {

    @Override
    public IntSet defaultValue() {
        return new HashIntSet();
    }

    @Override
    public IntSet handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        IntSet result = new HashIntSet();
        ResultSet resultSet = queryDescriptor.getResultSet();
        while (resultSet.next()) {
            result.add(resultSet.getInt(1));
        }
        return result;
    }

    @Override
    public IntSet handleType(ResultSet resultSet, Class<?> type) {
        return new HashIntSet();
    }

    @Override
    public IntSet handleColumn(ResultSet resultSet, int column) {
        return new HashIntSet();
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, IntSet arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String type() {
        return IntSet.class.getName();
    }
}
