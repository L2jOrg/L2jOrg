package org.l2j.commons.database.helpers;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.pair.IntObjectPair;
import org.l2j.commons.database.handler.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.util.Objects.isNull;

public class IndexedValuesStrategy implements MapParameterStrategy {

    private final IntObjectMap<IntObjectPair<Class<?>>> parametersInfo;

    public IndexedValuesStrategy(IntObjectMap<IntObjectPair<Class<?>>> parameters) {
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
                handler.setParameter(statement, parameterIndex, args[argumentIndex]);
            }
        }
    }
}
