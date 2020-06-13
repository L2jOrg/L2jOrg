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

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.database.helpers.QueryDescriptor;

import java.lang.reflect.ParameterizedType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public class IntMapHandler implements TypeHandler<IntMap<?>> {

    @Override
    public IntMap<?> defaultValue() {
        return new HashIntMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public IntMap<?> handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        Class<?> genericType = (Class<?>) ((ParameterizedType)queryDescriptor.getGenericReturnType()).getActualTypeArguments()[0];

        var handler = MAP.getOrDefault(genericType.getName(), MAP.get(Object.class.getName()));
        if(isNull(handler)) {
            throw new IllegalStateException("There is no TypeHandler to Type " + genericType);
        }

        HashIntMap<Object> result = new HashIntMap<>();
        var resultSet = queryDescriptor.getResultSet();
        while (resultSet.next()) {
            result.put(resultSet.getInt(1), handler.handleType(resultSet, genericType));
        }
        return result;
    }

    @Override
    public IntMap<?> handleType(ResultSet resultSet, Class<?> type) {
        return new HashIntMap<>();
    }

    @Override
    public IntMap<?> handleColumn(ResultSet resultSet, int column)  {
        return new HashIntMap<>();
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, IntMap<?> arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String type() {
        return IntMap.class.getName();
    }
}
