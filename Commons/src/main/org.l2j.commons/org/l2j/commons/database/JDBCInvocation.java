package org.l2j.commons.database;

import org.l2j.commons.database.annotation.Query;
import org.l2j.commons.database.handler.TypeHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ServiceLoader;

import static java.util.Objects.isNull;

class JDBCInvocation implements InvocationHandler {

    JDBCInvocation() {
        for (TypeHandler typeHandler : ServiceLoader.load(TypeHandler.class)) {
            TypeHandler.MAP.put(typeHandler.type(), typeHandler);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        var handler = TypeHandler.MAP.getOrDefault(method.getReturnType().getName(), TypeHandler.MAP.get(Object.class.getName()));

        if(isNull(handler)) {
            throw  new IllegalStateException("There is no TypeHandler Service");
        }

        if(!method.isAnnotationPresent(Query.class)){
            return handler.defaultValue();
        }

        var query = buildQuery(method, args);

        try(var con = L2DatabaseFactory.getInstance().getConnection();
            var statement = con.prepareStatement(query.toSql())) {
            statement.execute();
            query.setStatement(statement);
            return handler.handleResult(query);
        }
    }

    private QueryDescriptor buildQuery(Method method, Object[] args) {
        var query = method.getAnnotation(Query.class).value();
        var parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            var parameter = parameters[i];
            var isString = parameter.getType().isAssignableFrom(String.class);
            query = query.replaceAll(String.format(":%s:", parameter.getName()), isString ? String.format("'%s'", args[i]) : args[i].toString());
        }
        return new QueryDescriptor(method, query);
    }

}
