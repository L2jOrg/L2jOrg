package org.l2j.commons.database.handler;

import org.l2j.commons.database.helpers.QueryDescriptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
    public Void handleType(ResultSet resultSet, Class<?> type) {
        return null;
    }

    @Override
    public Void handleColumn(ResultSet resultSet, int column) {
        return null;
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, Void arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String type() {
        return Void.TYPE.getName();
    }
}
