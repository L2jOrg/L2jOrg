package org.l2j.commons.database;

/**
 * @author JoeAlisson
 * @param <T> entity type
 */
public interface DAO<T> {

    boolean save(T model);
}
