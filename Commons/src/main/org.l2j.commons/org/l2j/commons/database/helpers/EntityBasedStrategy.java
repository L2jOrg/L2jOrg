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
        var clazz = entity.getClass();
        try {
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
