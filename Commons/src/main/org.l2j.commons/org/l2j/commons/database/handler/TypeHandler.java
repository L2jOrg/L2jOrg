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
    T handle(ResultSet resultSet, Class<?> type) throws SQLException;
    String type();
}

