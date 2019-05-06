package org.l2j.commons.database;

public interface DAO<T> {

    boolean save(T model);
}
