package org.l2j.commons.database.helpers;

import io.github.joealisson.primitive.pair.IntObjectPair;
import org.l2j.commons.database.handler.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import static java.util.Objects.isNull;

public class EntityBasedStrategy implements MapParameterStrategy {

    private final Map<String, IntObjectPair<Class<?>>> parametersInfo;

    public EntityBasedStrategy(Map<String, IntObjectPair<Class<?>>> parametersInfo) {
        this.parametersInfo = parametersInfo;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setParameters(PreparedStatement statement, Object[] args) throws SQLException {
        if(isNull(args) || args.length < 1 || isNull(args[0])) {
            return;
        }

        var entity = args[0];
        var clazz = entity.getClass();

        try {
            for (var parameterInfo : parametersInfo.entrySet()) {
                var field = clazz.getDeclaredField(parameterInfo.getKey());
                if(field.trySetAccessible()) {
                    var type = parameterInfo.getValue().getValue();
                    var argumentIndex = parameterInfo.getValue().getKey();
                    var handler = TypeHandler.MAP.getOrDefault(type.getName(), TypeHandler.MAP.get(Object.class.getName()));
                    handler.setParameter(statement, argumentIndex, field.get(entity));
                } else {
                    throw  new SQLException("No accessible field " + field.getName() + " On type " + clazz );
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new SQLException(e);
        }


    }
}
