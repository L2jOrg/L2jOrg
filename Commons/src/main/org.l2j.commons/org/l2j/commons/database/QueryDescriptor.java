package org.l2j.commons.database;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.regex.Pattern;

public class QueryDescriptor {

    private static final Pattern SELECT_PATTERN = Pattern.compile("^SELECT.*", Pattern.CASE_INSENSITIVE);

    private final String query;
    private final Method method;
    private PreparedStatement statement;


    public QueryDescriptor(Method method, String query) {
        this.query = query;
        this.method = method;
    }

    public String toSql() {
        return query;
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

    public void setStatement(PreparedStatement statement) {
        this.statement = statement;
    }

    public Statement getStatement() {
        return statement;
    }
}
