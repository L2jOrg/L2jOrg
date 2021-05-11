/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.commons.database.handler;

import org.l2j.commons.database.QueryDescriptor;
import org.l2j.commons.database.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class InstantHandler implements TypeHandler<Instant> {
    @Override
    public Instant defaultValue() {
        return null;
    }

    @Override
    public Instant handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        var resultSet = queryDescriptor.getResultSet();
        if(resultSet.next()) {
            handleColumn(resultSet, 1);
        }
        return defaultValue();
    }

    @Override
    public Instant handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        return handleColumn(resultSet, 1);
    }

    @Override
    public Instant handleColumn(ResultSet resultSet, int column) throws SQLException {
        var timestamp = resultSet.getTimestamp(column);
        return nonNull(timestamp) ? timestamp.toInstant() : null;
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, Instant instant) throws SQLException {
        statement.setTimestamp(parameterIndex, nonNull(instant) ? Timestamp.from(instant) : null);
    }

    @Override
    public String type() {
        return Instant.class.getName();
    }
}
