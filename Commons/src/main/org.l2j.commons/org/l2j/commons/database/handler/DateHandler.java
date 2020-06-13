/*
 * Copyright © 2019-2020 L2JOrg
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

import org.l2j.commons.database.helpers.QueryDescriptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class DateHandler implements TypeHandler<LocalDate> {

    @Override
    public LocalDate defaultValue() {
        return null;
    }

    @Override
    public LocalDate handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        var resultSet = queryDescriptor.getResultSet();
        if(resultSet.next()) {
            return handleColumn(resultSet, 1);
        }
        return defaultValue();
    }

    @Override
    public LocalDate handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        return handleColumn(resultSet, 1);
    }

    @Override
    public LocalDate handleColumn(ResultSet resultSet, int column) throws SQLException {
        var date = resultSet.getDate(column);
        return  nonNull(date) ? date.toLocalDate() : null;
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, LocalDate date) throws SQLException {
        statement.setObject(parameterIndex, date);
    }

    @Override
    public String type() {
        return LocalDate.class.getName();
    }
}
