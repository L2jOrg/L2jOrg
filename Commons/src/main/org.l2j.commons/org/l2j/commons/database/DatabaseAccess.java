package org.l2j.commons.database;

import org.l2j.commons.cache.CacheFactory;
import org.l2j.commons.dao.JdbcDAO;
import org.l2j.commons.database.annotation.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.Repository;

import javax.cache.Cache;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

public class DatabaseAccess {

    private static Logger logger = LoggerFactory.getLogger(DatabaseAccess.class);

    //private static Cache<Class, DAO> cache;
    private static Map<Class, DAO> cache = new HashMap<>();
    private static JDBCInvocation handler = new JDBCInvocation();


    private static Map<Class<? extends Repository>, Repository> repositories = new LinkedHashMap<>();

    public static <T extends DAO> T getDAO(Class<T> daoClass) {
        if(cache.containsKey(daoClass)) {
            return daoClass.cast(cache.get(daoClass));
        }

        var dao =  daoClass.cast(Proxy.newProxyInstance(daoClass.getClassLoader(), new Class[]{ daoClass }, handler));
        cache.put(daoClass, dao);
        return dao;
    }

    public static <T extends Repository> T getRepository(Class<T> repositoryClass) {
        if(repositories.containsKey(repositoryClass)) {
            return repositoryClass.cast(repositories.get(repositoryClass));
        }
        T repository = null;
        try {
            repository = L2DatabaseFactory.getInstance().getRepository(repositoryClass);
            if(nonNull(repository)) {
                repositories.put(repositoryClass, repository);
            }
        } catch (Exception e) {
            logger.error("Error accessing Database", e);
        }
        return repository;
    }

    public static void shutdown() {
        try {
            L2DatabaseFactory.getInstance().shutdown();
        } catch (SQLException e) {
            logger.warn(e.getLocalizedMessage(), e);
        }
    }
}
