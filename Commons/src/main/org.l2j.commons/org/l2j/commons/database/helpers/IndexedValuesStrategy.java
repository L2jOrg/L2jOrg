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
