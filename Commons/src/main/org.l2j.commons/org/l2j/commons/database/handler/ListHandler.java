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

import org.l2j.commons.database.HandlersSupport;
import org.l2j.commons.database.QueryDescriptor;
import org.l2j.commons.database.TypeHandler;

import java.lang.reflect.ParameterizedType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class ListHandler implements TypeHandler<List<?>> {

    @Override
    public List<?> defaultValue() {
        return new ArrayList<>();
    }

    @Override
    public List<?> handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        return handleResultAndThen(queryDescriptor, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<?> handleResultAndThen(QueryDescriptor queryDescriptor, Consumer<Object> typeConsumer) throws SQLException {
        Class<?> genericType = (Class<?>) ((ParameterizedType)queryDescriptor.getGenericReturnType()).getActualTypeArguments()[0];

        var handler = HandlersSupport.handlerFromClass(genericType);
        if(isNull(handler)) {
            throw new IllegalStateException("There is no TypeHandler to Type " + genericType);
        }
        List<Object> result = new ArrayList<>();
        var resultSet = queryDescriptor.getResultSet();
        while (resultSet.next()) {
            var entry = handler.handleType(resultSet, genericType);
            if(nonNull(typeConsumer)) {
                typeConsumer.accept(entry);
            }
            result.add(entry);
        }
        return result;
    }

    @Override
    public List<?> handleType(ResultSet resultSet, Class<?> type) {
        return new ArrayList<>();
    }

    @Override
    public List<?> handleColumn(ResultSet resultSet, int column) {
        return new ArrayList<>();
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, List<?> arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String type() {
        return List.class.getName();
    }
}
