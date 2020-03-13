package org.l2j.commons.database.handler;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.ConcurrentIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.database.helpers.QueryDescriptor;

import java.lang.reflect.ParameterizedType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public class ConcurrentIntMapHandler implements TypeHandler<IntMap<?>> {

    @Override
    public IntMap<?> defaultValue() {
        return new CHashIntMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public IntMap<?> handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        Class<?> genericType = (Class<?>) ((ParameterizedType)queryDescriptor.getGenericReturnType()).getActualTypeArguments()[0];

        var handler = MAP.getOrDefault(genericType.getName(), MAP.get(Object.class.getName()));
        if(isNull(handler)) {
            throw new IllegalStateException("There is no TypeHandler to Type " + genericType);
        }
        ConcurrentIntMap<Object> result = new CHashIntMap<>();
        var resultSet = queryDescriptor.getResultSet();
        while (resultSet.next()) {
            result.put(resultSet.getInt(1), handler.handleType(resultSet, genericType));
        }
        return result;
    }

    @Override
    public IntMap<?> handleType(ResultSet resultSet, Class<?> type) {
        return new CHashIntMap<>();
    }

    @Override
    public IntMap<?> handleColumn(ResultSet resultSet, int column)  {
        return new CHashIntMap<>();
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, IntMap<?> arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String type() {
        return ConcurrentIntMap.class.getName();
    }
}
