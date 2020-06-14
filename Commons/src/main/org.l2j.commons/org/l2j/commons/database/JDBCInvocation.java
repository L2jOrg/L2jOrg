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
package org.l2j.commons.database;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntKeyValue;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.cache.CacheFactory;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.commons.database.annotation.Query;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.handler.TypeHandler;
import org.l2j.commons.database.helpers.EntityBasedStrategy;
import org.l2j.commons.database.helpers.QueryDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.Cache;
import java.lang.reflect.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.*;

/**
 * @author JoeAlisson
 */
class JDBCInvocation implements InvocationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JDBCInvocation.class);
    private static final Pattern PARAMETER_PATTERN = Pattern.compile(":(\\w+?):");
    private static final String INSERT_TEMPLATE = "INSERT INTO %s %s VALUES %s ON DUPLICATE KEY UPDATE %s";
    private static final String COLUMN_PATTERN = "(`\\w+`)";
    private static final String DUPLICATE_UPDATE_PATTERN = "$1=VALUES($1)";
    private static final String ESCAPE_KEYWORD = "`";

    private static final Cache<Method, QueryDescriptor> descriptors = CacheFactory.getInstance().getCache("sql-descriptors");
    private static final Cache<Class<?>, QueryDescriptor> saveDescriptors = CacheFactory.getInstance().getCache("sql-save-descriptors");

    JDBCInvocation() {
        for (TypeHandler<?> typeHandler : ServiceLoader.load(TypeHandler.class)) {
            TypeHandler.MAP.put(typeHandler.type(), typeHandler);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getName().equalsIgnoreCase("save") && method.getParameterCount() == 1) {
            return save(method, args);
        }

        var handler = TypeHandler.MAP.getOrDefault(method.getReturnType().isEnum() ? "enum" : method.getReturnType().getName(), TypeHandler.MAP.get(Object.class.getName()));

        if(isNull(handler)) {
            throw new IllegalStateException("There is no TypeHandler Service for type " + method.getReturnType().getName());
        }

        if(!method.isAnnotationPresent(Query.class)) {
            return handler.defaultValue();
        }

        try(var query = buildQuery(method);
            var con = DatabaseFactory.getInstance().getConnection()) {
            query.execute(con, args);
            if(hasResultConsumer(method)) {
                var consumer = resultSetConsumer(args);
                if(nonNull(consumer)) {
                    consumer.accept(query.getResultSet());
                }  else {
                    LOGGER.warn("Should be a consumer on last parameter of method {}", method);
                }
                return null;
            } else {
                return handler.handleResult(query);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Consumer<ResultSet> resultSetConsumer(Object[] args) {
        var consumer = args[args.length-1];
        if(consumer instanceof Consumer) {
            return  (Consumer<ResultSet>) consumer;
        }
        return null;
    }

    private boolean hasResultConsumer(Method method) {
        var size = method.getParameterCount();
        return size >= 1 && method.getParameterTypes()[size -1] == Consumer.class;
    }

    private boolean save(Method method, Object[] args) throws SQLException {
        if(args.length < 1 || isNull(args[0])) {
            return false;
        }

        var isBatch = false;
        Class<?> clazz = args[0].getClass();
        if(Collection.class.isAssignableFrom(clazz)) {
            if(((Collection<?>) args[0]).isEmpty()) {
                return false;
            }
            clazz = (Class<?>) ((ParameterizedType) method.getGenericParameterTypes()[0]).getActualTypeArguments()[0];
            isBatch = true;
        }

        var table = clazz.getAnnotation(Table.class);

        if(isNull(table)) {
            LOGGER.error("The class {} must be annotated with @Table to save it", args[0].getClass());
            return false;
        }

        try(var con = DatabaseFactory.getInstance().getConnection();
            var query = buildSaveQuery(clazz, method, table) ) {
            if(isBatch) {
                query.executeBatch(con, (Collection<?>) args[0]);
            } else {
                query.execute(con, args);
                if (isNotEmpty(table.autoGeneratedProperty())) {
                    trySetGeneratedKey(args, clazz, table, query);
                }
            }
            return true;
        }
    }

    private void trySetGeneratedKey(Object[] args, Class<?> clazz, Table table, QueryDescriptor query)  {
        var field = findField(clazz, table.autoGeneratedProperty());
        if(nonNull(field)) {
            try {
                var key = query.getGeneratedKey();
                field.trySetAccessible();
                field.set(args[0], key);
            } catch (Exception e) {
                LOGGER.warn("Couldn't set generated key to entity", e);
            }
        }
    }

    private QueryDescriptor buildSaveQuery(Class<?> clazz, Method method, Table table) {
        if(saveDescriptors.containsKey(clazz)) {
            return saveDescriptors.get(clazz);
        }

        var fields = fieldsOf(clazz);
        Map<String, IntKeyValue<Class<?>>> parameterMap = new HashMap<>(fields.size());

        var columns = fields.stream().filter(f -> !f.isAnnotationPresent(NonUpdatable.class) && !Modifier.isStatic(f.getModifiers()))
                .peek(f -> parameterMap.put(f.getName(), new IntKeyValue<>(parameterMap.size()+1, f.getType())))
                .map(this::fieldToColumnName).collect(Collectors.joining(","));


        var values = "?".repeat(parameterMap.size()).chars().mapToObj(Character::toString).collect(Collectors.joining(",", "(", ")"));
        var update = columns.replaceAll(COLUMN_PATTERN, DUPLICATE_UPDATE_PATTERN);
        var query = new QueryDescriptor(method, String.format(INSERT_TEMPLATE, table.value(), "(" + columns + ")", values, update), new EntityBasedStrategy(parameterMap));

        saveDescriptors.put(clazz, query);
        return query;
    }

    private String fieldToColumnName(Field field) {
        return ESCAPE_KEYWORD + (field.isAnnotationPresent(Column.class) ? field.getAnnotation(Column.class).value() : field.getName()) + ESCAPE_KEYWORD;
    }

    private QueryDescriptor buildQuery(final Method method)  {
        if(descriptors.containsKey(method)) {
            return descriptors.get(method);
        }
        var descriptor = buildDescriptor(method);
        descriptors.put(method, descriptor);

        return descriptor;
    }

    private QueryDescriptor buildDescriptor(Method method) {
        var query = method.getAnnotation(Query.class).value();
        if(method.getParameters().length == 0) {
            return new QueryDescriptor(method, query);
        }

        var matcher = PARAMETER_PATTERN.matcher(query);
        var parameterMapper = mapParameters(method.getParameters());
        IntMap<IntKeyValue<Class<?>>> parameters = new HashIntMap<>();

        var parameterCount = 0;
        while (matcher.find()) {
            if(!parameterMapper.containsKey(matcher.group(1))) {
                LOGGER.error("There is no correspondent parameter to variable {} on method {}#{}", matcher.group(1), method.getDeclaringClass().getName(), method.getName());
                parameters.put(++parameterCount, null);
            } else {
                parameters.put(++parameterCount, parameterMapper.get(matcher.group(1)));
            }
        }
        return new QueryDescriptor(method, matcher.replaceAll("?"), parameters);
    }

    private Map<String, IntKeyValue<Class<?>>> mapParameters(Parameter[] parameters) {
        Map<String, IntKeyValue<Class<?>>> parameterMap = new HashMap<>(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            var parameter = parameters[i];
            parameterMap.put(parameter.getName(), new IntKeyValue<>(i, parameter.getType()));
        }
        return parameterMap;
    }

}
