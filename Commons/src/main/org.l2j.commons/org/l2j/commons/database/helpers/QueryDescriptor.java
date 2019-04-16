package org.l2j.commons.database.helpers;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.pair.IntObjectPair;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;

public class QueryDescriptor implements AutoCloseable {

    private static final Pattern SELECT_PATTERN = Pattern.compile("^SELECT.*", Pattern.CASE_INSENSITIVE);
    private static final NoParameterStrategy NO_PARAMETER_STRATEGY = new NoParameterStrategy();

    private final String query;
    private final Method method;
    private final MapParameterStrategy strategy;
    private PreparedStatement statement;


    public QueryDescriptor(Method method, String query) {
        this(method, query, NO_PARAMETER_STRATEGY);
    }

    public QueryDescriptor(Method method, String query, IntObjectMap<IntObjectPair<Class<?>>> parametersInfo) {
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

    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    public Type getGenericReturnType() {
        return method.getGenericReturnType();
    }

    public ResultSet getResultSet() throws SQLException {
        return statement.getResultSet();
    }

    @Override
    public void close() throws SQLException {
        if(nonNull(statement)) {
            statement.close();
        }
    }

    public void execute(Connection con, Object[] args) throws SQLException {
        statement = con.prepareStatement(query);
        strategy.setParameters(statement, args);
        statement.execute();
    }

    public Integer getUpdateCount() throws SQLException {
        return statement.getUpdateCount();
    }
}
