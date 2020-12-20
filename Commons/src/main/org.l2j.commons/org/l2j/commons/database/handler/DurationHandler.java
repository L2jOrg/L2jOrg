package org.l2j.commons.database.handler;

import org.l2j.commons.database.QueryDescriptor;
import org.l2j.commons.database.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

public class DurationHandler implements TypeHandler<Duration> {

    @Override
    public Duration defaultValue() {
        return Duration.ZERO;
    }

    @Override
    public Duration handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        var resultSet = queryDescriptor.getResultSet();
        if(resultSet.next()) {
            return handleColumn(resultSet, 1);
        }
        return defaultValue();
    }

    @Override
    public Duration handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        return handleColumn(resultSet, 1);
    }

    @Override
    public Duration handleColumn(ResultSet resultSet, int column) throws SQLException {
        final var duration = resultSet.getLong(column);
        return Duration.ofSeconds(duration);
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, Duration duration) throws SQLException {
        statement.setLong(parameterIndex, duration.toSeconds());
    }

    @Override
    public String type() {
        return Duration.class.getName();
    }
}