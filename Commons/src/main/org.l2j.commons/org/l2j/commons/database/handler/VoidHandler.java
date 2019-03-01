package org.l2j.commons.database.handler;

import org.l2j.commons.database.QueryDescriptor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VoidHandler implements TypeHandler<Void> {

    @Override
    public Void defaultValue() {
        return null;
    }

    @Override
    public Void handleResult(QueryDescriptor queryDescriptor) {
        return null;
    }

    @Override
    public Void handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        return null;
    }

    @Override
    public Void handleColumn(ResultSet resultSet, int column) throws SQLException {
        return null;
    }

    @Override
    public String type() {
        return Void.TYPE.getName();
    }
}
