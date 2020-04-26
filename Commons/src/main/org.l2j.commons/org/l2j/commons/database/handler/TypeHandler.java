package org.l2j.commons.database.handler;

import org.l2j.commons.database.helpers.QueryDescriptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author JoeAlisson
 * @param <T> the entity type
 */
public interface TypeHandler<T> {

    @SuppressWarnings("rawTypes")
    Map<String, TypeHandler> MAP = new HashMap<>();

    T defaultValue();
    T handleResult(QueryDescriptor queryDescriptor) throws SQLException;
    T handleType(ResultSet resultSet, Class<?> type) throws SQLException;
    T handleColumn(ResultSet resultSet, int column) throws SQLException;

    void setParameter(PreparedStatement statement, int parameterIndex, T arg) throws SQLException;
    String type();

    default T handleColumn(ResultSet resultSet, int column, Class<?> type) throws SQLException {
        return handleColumn(resultSet, column);
    }
}

