package org.l2j.commons.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseAccess {

    private static Logger logger = LoggerFactory.getLogger(DatabaseAccess.class);

    private static Map<Class, DAO> cache = new HashMap<>();
    private static JDBCInvocation handler = new JDBCInvocation();


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
            L2DatabaseFactory.getInstance().shutdown();
        } catch (SQLException e) {
            logger.warn(e.getLocalizedMessage(), e);
        }
    }
}
