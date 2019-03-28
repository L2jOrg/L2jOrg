package org.l2j.commons.database;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.pair.IntObjectPair;
import org.l2j.commons.database.handler.TypeHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class QueryDescriptor implements AutoCloseable {

    private static final Pattern SELECT_PATTERN = Pattern.compile("^SELECT.*", Pattern.CASE_INSENSITIVE);

    private final String query;
    private final Method method;
    private final IntObjectMap<IntObjectPair<Class<?>>> parametersInfo;
    private PreparedStatement statement;
    private ResultSet resultSet;


    public QueryDescriptor(Method method, String query) {
        this(method, query, null);
    }

    public QueryDescriptor(Method method, String query, IntObjectMap<IntObjectPair<Class<?>>> parametersInfo) {
        this.query = query;
        this.method = method;
        this.parametersInfo = parametersInfo;
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

        if(nonNull(resultSet)) {
            resultSet.close();
        }
    }


    public void execute(Connection con, Object[] args) throws SQLException {
        statement = con.prepareStatement(query);
        if(nonNull(parametersInfo)) {
            setParameters(args);
        }
        statement.execute();
    }

    @SuppressWarnings("unchecked")
    private void setParameters(Object[] args) throws SQLException {
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

    public Integer getUpdateCount() throws SQLException {
        return statement.getUpdateCount();
    }
}
