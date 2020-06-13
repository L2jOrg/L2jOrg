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
package org.l2j.commons.database.helpers;

import io.github.joealisson.primitive.IntKeyValue;
import org.l2j.commons.database.handler.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class EntityBasedStrategy implements MapParameterStrategy {

    private final Map<String, IntKeyValue<Class<?>>> parametersInfo;

    public EntityBasedStrategy(Map<String, IntKeyValue<Class<?>>> parametersInfo) {
        this.parametersInfo = parametersInfo;
    }

    @Override
    public void setParameters(PreparedStatement statement, Object[] args) throws SQLException {
        if(isNull(args) || args.length < 1 || isNull(args[0])) {
            return;
        }
        setParameters(statement, args[0]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setParameters(PreparedStatement statement, Object entity) throws SQLException {
        try {
            var clazz = entity.getClass();
            for (var parameterInfo : parametersInfo.entrySet()) {
                var field = clazz.getDeclaredField(parameterInfo.getKey());
                if(field.trySetAccessible()) {
                    var argumentIndex = parameterInfo.getValue().getKey();
                    var value = field.get(entity);
                    var type = parameterInfo.getValue().getValue();
                    var handler = TypeHandler.MAP.getOrDefault(type.isEnum() ? "enum" : type.getName(), TypeHandler.MAP.get(Object.class.getName()));
                    if(nonNull(value)) {
                        handler.setParameter(statement, argumentIndex, value);
                    } else {
                        handler.setParameter(statement, argumentIndex, handler.defaultValue());
                    }
                } else {
                    throw new SQLException("No accessible field " + field.getName() + " On type " + clazz );
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new SQLException(e);
        }

    }
}
