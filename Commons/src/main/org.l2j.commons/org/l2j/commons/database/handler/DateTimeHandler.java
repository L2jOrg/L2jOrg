package org.l2j.commons.database.handler;

import org.l2j.commons.database.helpers.QueryDescriptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

import static java.util.Objects.nonNull;

public class DateTimeHandler implements TypeHandler<LocalDateTime> {

    @Override
    public LocalDateTime defaultValue() {
        return null;
    }

    @Override
    public LocalDateTime handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        var resultSet = queryDescriptor.getResultSet();
        if(resultSet.next()) {
            return handleColumn(resultSet, 1);
        }
        return defaultValue();
    }

    @Override
    public LocalDateTime handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        return handleColumn(resultSet, 1);
    }

    @Override
    public LocalDateTime handleColumn(ResultSet resultSet, int column) throws SQLException {
        var timestamp = resultSet.getTimestamp(column);
        return nonNull(timestamp) ? timestamp.toLocalDateTime() : null;
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, LocalDateTime date) throws SQLException {
        statement.setObject(parameterIndex, date);
    }

    @Override
    public String type() {
        return LocalDateTime.class.getName();
    }
}
