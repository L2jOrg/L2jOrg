package org.l2j.commons.database;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import io.github.joealisson.primitive.pair.IntObjectPair;
import io.github.joealisson.primitive.pair.impl.ImmutableIntObjectPairImpl;
import org.l2j.commons.database.annotation.Query;
import org.l2j.commons.database.handler.TypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

class JDBCInvocation implements InvocationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JDBCInvocation.class);
    private static final Pattern PARAMETER_PATTERN = Pattern.compile(":(.*?):");
    // TODO use cache API
    private static final Map<Method, QueryDescriptor> descriptors = new HashMap<>();

    JDBCInvocation() {
        for (TypeHandler typeHandler : ServiceLoader.load(TypeHandler.class)) {
            TypeHandler.MAP.put(typeHandler.type(), typeHandler);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        var handler = TypeHandler.MAP.getOrDefault(method.getReturnType().getName(), TypeHandler.MAP.get(Object.class.getName()));

        if(isNull(handler)) {
            throw new IllegalStateException("There is no TypeHandler Service");
        }

        if(!method.isAnnotationPresent(Query.class)) {
            return handler.defaultValue();
        }

        try(var con = DatabaseFactory.getInstance().getConnection();
            var query = buildQuery(method)) {
            query.execute(con, args);
            return handler.handleResult(query);
        }
    }

    private QueryDescriptor buildQuery(final Method method)  {
        return descriptors.computeIfAbsent(method, this::buildDescriptor);
    }


    private QueryDescriptor buildDescriptor(Method method) {
        var query = method.getAnnotation(Query.class).value();
        if(method.getParameters().length == 0) {
            return new QueryDescriptor(method, query);
        }

        var matcher = PARAMETER_PATTERN.matcher(query);

        var parameterMapper = mapParameters(method.getParameters());

        IntObjectMap<IntObjectPair<Class<?>>> parameters = new HashIntObjectMap<>();

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

    private Map<String, IntObjectPair<Class<?>>> mapParameters(Parameter[] parameters) {
        Map<String, IntObjectPair<Class<?>>> parameterMap = new HashMap<>(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            var parameter = parameters[i];
            parameterMap.put(parameter.getName(), new ImmutableIntObjectPairImpl<>(i, parameter.getType()));
        }
        return parameterMap;
    }

}
