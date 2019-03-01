package org.l2j.commons.database.handler;

import org.l2j.commons.database.QueryDescriptor;

import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public class ListHandler implements TypeHandler<List> {

    @Override
    public List defaultValue() {
        return new ArrayList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        Class<?> genericType = (Class<?>) ((ParameterizedType)queryDescriptor.getGenericReturnType()).getActualTypeArguments()[0];

        var handler = MAP.getOrDefault(genericType.getName(), MAP.get(Object.class.getName()));
        if(isNull(handler)) {
            throw new IllegalStateException("There is no TypeHandler to Type " + genericType);
        }
        List<Object> result = new ArrayList<>();
        var resultSet = queryDescriptor.getStatement().getResultSet();
        while (resultSet.next()) {
            result.add(handler.handleType(resultSet, genericType));
        }
        return result;
    }

    @Override
    public List handleType(ResultSet resultSet, Class<?> type) {
        return new ArrayList();
    }

    @Override
    public List handleColumn(ResultSet resultSet, int column) throws SQLException {
        return new ArrayList();
    }

    @Override
    public String type() {
        return List.class.getName();
    }
}
