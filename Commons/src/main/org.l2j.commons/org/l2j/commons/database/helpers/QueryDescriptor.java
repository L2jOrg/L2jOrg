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
import org.l2j.commons.database.annotation.Query;
import org.l2j.commons.database.helpers.BatchSupporters.BatchSupport;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.Collection;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class QueryDescriptor implements AutoCloseable {

    private static final Pattern SELECT_PATTERN = Pattern.compile("^SELECT.*", Pattern.CASE_INSENSITIVE);
    private static final NoParameterStrategy NO_PARAMETER_STRATEGY = new NoParameterStrategy();
    private static final ThreadLocal<Statement> statementLocal = new ThreadLocal<>();

    private final String query;
    private final Method method;
    private final MapParameterStrategy strategy;

    public QueryDescriptor(Method method, String query) {
        this(method, query, NO_PARAMETER_STRATEGY);
    }

    public QueryDescriptor(Method method, String query, IntMap<IntKeyValue<Class<?>>> parametersInfo) {
        this(method, query, new IndexedValuesStrategy(parametersInfo));
    }

    public QueryDescriptor(Method method, String query, MapParameterStrategy strategy) {
        this.query = query;
        this.method = method;
        this.strategy = strategy;
    }

    public boolean isUpdate() {
        return !SELECT_PATTERN.matcher(query).matches();
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

    @Override
    public void close() throws SQLException {
        var statement = statementLocal.get();
        if(nonNull(statement)) {
            statement.close();
            statementLocal.remove();
        }
    }

    public void execute(Connection con, Object[] args) throws SQLException {
        var statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        if(isBatch(args)) {
            executeBatch(statement, args);
        } else {
            executeSingle(statement, args);
        }
        statementLocal.set(statement);
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
        var statment = statementLocal.get();
        if(nonNull(statment)) {
            var rs = statment.getGeneratedKeys();
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
