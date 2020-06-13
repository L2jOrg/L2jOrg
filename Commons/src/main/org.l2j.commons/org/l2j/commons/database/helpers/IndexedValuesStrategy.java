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
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.database.handler.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public class IndexedValuesStrategy implements MapParameterStrategy {

    private final IntMap<IntKeyValue<Class<?>>> parametersInfo;

    public IndexedValuesStrategy(IntMap<IntKeyValue<Class<?>>> parameters) {
        this.parametersInfo =  parameters;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setParameters(PreparedStatement statement, Object[] args) throws SQLException {
        for (var parameterInfo : parametersInfo.entrySet()) {
            var parameterIndex = parameterInfo.getKey();
            if (isNull(parameterInfo.getValue())) {
                statement.setString(parameterIndex, "NULL");
            } else {
                var type = parameterInfo.getValue().getValue();
                var argumentIndex = parameterInfo.getValue().getKey();
                var handler = TypeHandler.MAP.getOrDefault(type.getName(), TypeHandler.MAP.get(Object.class.getName()));
                if(argumentIndex < args.length) {
                    handler.setParameter(statement, parameterIndex, args[argumentIndex]);
                } else {
                    statement.setString(parameterIndex, "NULL");
                }
            }
        }
    }

    @Override
    public void setParameters(PreparedStatement statement, Object obj) throws SQLException {
        setParameters(statement, new Object[] { obj });
    }
}
