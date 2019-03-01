package org.l2j.commons.database.handler;

import org.l2j.commons.database.QueryDescriptor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public interface TypeHandler<T> {

    Map<String, TypeHandler> MAP = new HashMap<>();

    T defaultValue();
    T handleResult(QueryDescriptor queryDescriptor) throws SQLException;
    T handleType(ResultSet resultSet, Class<?> type) throws SQLException;
    T handleColumn(ResultSet resultSet, int column) throws SQLException;

    String type();
}

