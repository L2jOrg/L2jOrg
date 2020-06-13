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

