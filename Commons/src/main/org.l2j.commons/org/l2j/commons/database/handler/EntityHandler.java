package org.l2j.commons.database.handler;

import org.l2j.commons.database.helpers.QueryDescriptor;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class EntityHandler implements TypeHandler<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityHandler.class);

    @Override
    public Object defaultValue() {
        return null;
    }

    @Override
    public Object handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        var resultSet = queryDescriptor.getResultSet();
        if(nonNull(resultSet) && resultSet.next()) {
            return handleType(resultSet, queryDescriptor.getReturnType());
        }
        return defaultValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        try {
            var instance = type.getDeclaredConstructor().newInstance();
            var fields = Util.fieldsOf(type);

            var metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                var columnName = metaData.getColumnLabel(i);

                Field f = findField(fields, columnName);
                if(isNull(f)) {
                    LOGGER.debug("There is no field with name {} on Type {}",  columnName, type.getName());
                    continue;
                }
                if(f.trySetAccessible()) {
                    var handler = TypeHandler.MAP.getOrDefault(f.getType().isEnum() ? "enum" : f.getType().getName(), TypeHandler.MAP.get(Object.class.getName()));
                    f.set(instance, handler.handleColumn(resultSet, i, f.getType()));
                } else {
                    throw new SQLException("No accessible field " + f.getName() + " On type " + type );
                }
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Object handleColumn(ResultSet resultSet, int column) {
        return null;
    }

    private Field findField(List<Field> fields, String columnName) {
        for (Field field : fields) {
            if(field.isAnnotationPresent(Column.class)) {
                if(field.getAnnotation(Column.class).value().equalsIgnoreCase(columnName)) {
                    return field;
                }
            } else if(field.getName().equalsIgnoreCase(columnName)) {
                return field;
            }
        }
        return  null;
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String type() {
        return Object.class.getName();
    }
}
