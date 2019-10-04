package org.l2j.commons.database;

import java.util.Collection;

/**
 * @author JoeAlisson
 * @param <T> entity type
 */
public interface DAO<T> {

    boolean save(T model);

    boolean save(Collection<T> models);
}
