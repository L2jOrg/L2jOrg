package org.l2j.commons.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.Repository;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

public class DatabaseAccess {

    private static Logger logger = LoggerFactory.getLogger(DatabaseAccess.class);

    private static Map<Class<? extends Repository>, Repository> repositories = new LinkedHashMap<>();

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
