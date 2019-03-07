package org.l2j.commons.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseAccess {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseAccess.class);
    private static boolean initialized = false;

    private static Map<Class, DAO> cache = new HashMap<>();
    private static JDBCInvocation handler = new JDBCInvocation();

    public static boolean initialize() {
        if(initialized) {
            return true;
        }

        try {
            DatabaseFactory.getInstance();
            return true;
        } catch (SQLException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    public static <T extends DAO> T getDAO(Class<T> daoClass) {
        if(cache.containsKey(daoClass)) {
            return daoClass.cast(cache.get(daoClass));
        }

        var dao =  daoClass.cast(Proxy.newProxyInstance(daoClass.getClassLoader(), new Class[]{ daoClass }, handler));
        cache.put(daoClass, dao);
        return dao;
    }

    public static void shutdown() {
        try {
            DatabaseFactory.getInstance().shutdown();
        } catch (SQLException e) {
            LOGGER.warn(e.getLocalizedMessage(), e);
        }
    }
}
