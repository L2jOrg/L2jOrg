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
package org.l2j.commons.database;

import io.github.joealisson.primitive.IntKeyValue;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.database.annotation.Query;
import org.l2j.commons.database.helpers.BatchSupporters;
import org.l2j.commons.database.helpers.BatchSupporters.BatchSupport;
import org.l2j.commons.database.helpers.IndexedValuesStrategy;
import org.l2j.commons.database.helpers.MapParameterStrategy;
import org.l2j.commons.database.helpers.NoParameterStrategy;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class QueryDescriptor implements AutoCloseable {

    private static final Pattern SELECT_PATTERN = Pattern.compile("^SELECT.*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final NoParameterStrategy NO_PARAMETER_STRATEGY = new NoParameterStrategy();
    private static final ThreadLocal<Statement> statementLocal = new ThreadLocal<>();

    private final String query;
    private final Method method;
    private final MapParameterStrategy strategy;
    private final boolean isUpdate;
    private boolean hasResultSetConsumer;
    private boolean hasTypeConsumer;

    QueryDescriptor(Method method, String query) {
        this(method, query, NO_PARAMETER_STRATEGY);
    }

    QueryDescriptor(Method method, String query, IntMap<IntKeyValue<Class<?>>> parametersInfo) {
        this(method, query, new IndexedValuesStrategy(parametersInfo));
    }

    QueryDescriptor(Method method, String query, MapParameterStrategy strategy) {
        this.query = query;
        this.method = method;
        this.strategy = strategy;
        this.isUpdate = !SELECT_PATTERN.matcher(query).matches();

        var size = method.getParameterCount();
        if(size > 0 && method.getParameterTypes()[size -1] == Consumer.class) {
            if(ResultSet.class.isAssignableFrom((Class<?>)((ParameterizedType) method.getGenericParameterTypes()[size -1]).getActualTypeArguments()[0])) {
                hasResultSetConsumer =  true;
            } else {
                hasTypeConsumer = true;
            }
        }
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public boolean isBatch(Object[] args) {
        var batchIndex = getBatchIndex();

        if(batchIndex < 0 || batchIndex >= args.length) {
            return false;
        }
        return isBatchSupported(args[batchIndex]);
    }

    private int getBatchIndex() {
        final var queryAnnotation = method.getAnnotation(Query.class);
        return nonNull(queryAnnotation) ? queryAnnotation.batchIndex() : -1;
    }

    private boolean isBatchSupported(Object batchedArg) {
        return nonNull(supporterHandler(batchedArg.getClass()));
    }

    private BatchSupport supporterHandler(Class<?> arg) {
        return BatchSupporters.batchSupportHandler(arg);
    }

    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    public Type getGenericReturnType() {
        return method.getGenericReturnType();
    }

    public ResultSet getResultSet() throws SQLException {
        var statement = statementLocal.get();
        return nonNull(statement) ? statement.getResultSet() : null;
    }

    public boolean hasTypeConsumer() {
        return hasTypeConsumer;
    }

    public boolean hasResultSetConsumer() {
        return hasResultSetConsumer;
    }

    @Override
    public void close() throws SQLException {
        var statement = statementLocal.get();
        if(nonNull(statement)) {
            statement.close();
            statementLocal.remove();
        }
    }

    public void execute(Connection con, Object[] args) throws SQLException {
        var statement = createPreparedStatement(con);
        if(isBatch(args)) {
            executeBatch(statement, args);
        } else {
            executeSingle(statement, args);
        }
        statementLocal.set(statement);
    }

    private PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        var annotation = method.getAnnotation(Query.class);
        PreparedStatement st;
        if(nonNull(annotation) && annotation.scrollResult()) {
            st = con.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } else if(isUpdate) {
            st = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        } else {
            st = con.prepareStatement(query);
        }
        return st;
    }

    @SuppressWarnings("unchecked")
    private void executeBatch(PreparedStatement statement, Object[] args) throws SQLException {
        final var batchIndex = getBatchIndex();
        final var batchArg = args[batchIndex];
        final var supporter = supporterHandler(batchArg.getClass());
        final var handler = supporter.getHandler();
        final var iterator = supporter.getIterator(batchArg);
        var hasElement = false;

        while (iterator.hasNext()) {
            strategy.setParameters(statement, args);
            handler.setParameter(statement, batchIndex+1, iterator.next());
            statement.addBatch();
            hasElement = true;
        }
        if(hasElement){
            statement.executeBatch();
        }
    }

    private void executeSingle(PreparedStatement statement, Object[] args) throws SQLException {
        strategy.setParameters(statement, args);
        statement.execute();
    }

    public void executeBatch(Connection con, Collection<?> collection) throws SQLException {
        var statement = con.prepareStatement(query);
        for (Object obj : collection) {
            strategy.setParameters(statement, obj);
            statement.addBatch();
        }
        statement.executeBatch();
        statementLocal.set(statement);

    }

    public int getGeneratedKey() throws SQLException{
        var statement = statementLocal.get();
        if(nonNull(statement)) {
            var rs = statement.getGeneratedKeys();
            if(rs.next()) {
                return rs.getInt(1);
            }
        }
        return  0;
    }

    public Integer getUpdateCount() throws SQLException {
        var statement = statementLocal.get();
        return nonNull(statement) ? statement.getUpdateCount() : 0;
    }
}
