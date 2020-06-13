/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.commons.database;

import org.l2j.commons.cache.CacheFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.Cache;
import java.lang.reflect.Proxy;
import java.sql.SQLException;

/**
 * @author JoeAlisson
 */
public class DatabaseAccess {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseAccess.class);
    @SuppressWarnings("rawtypes")
    private static final Cache<Class, DAO> cache = CacheFactory.getInstance().getCache("dao", Class.class, DAO.class);
    private static final JDBCInvocation handler = new JDBCInvocation();

    private volatile static boolean initialized = false;

    public static boolean initialize() {
        if(initialized) {
            return true;
        }
        try {
            DatabaseFactory.getInstance();
            return initialized = true;
        } catch (SQLException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return initialized;
    }

    public static <T extends DAO<?>> T getDAO(Class<T> daoClass) {
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
