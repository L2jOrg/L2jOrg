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
package org.l2j.commons.cache;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.Duration;
import javax.cache.expiry.TouchedExpiryPolicy;
import javax.cache.spi.CachingProvider;
import java.nio.file.Path;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public final class CacheFactory {

    private CacheManager manager;

    private CacheFactory() {

    }

    public void initialize(String configurationFilePath) {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        manager = cachingProvider.getCacheManager(Path.of(configurationFilePath).toUri(), getClass().getClassLoader());
    }

    public <K, V> Cache<K, V> getCache(String alias) {
        checkInitilized();
        Cache<K, V> cache = manager.getCache(alias);
        if(isNull(cache)) {
           cache = manager.createCache(alias, new MutableConfiguration<K, V>().setStoreByValue(false).setExpiryPolicyFactory(TouchedExpiryPolicy.factoryOf(Duration.ONE_HOUR)));
        }
        return cache;
    }

    public <K, V> Cache<K, V> getCache(String alias, Class<K> keyClass, Class<V> valueClass) {
        checkInitilized();
        Cache<K, V> cache = manager.getCache(alias, keyClass, valueClass);
        if(isNull(cache)) {
            cache = manager.createCache(alias, new MutableConfiguration<K, V>().setTypes(keyClass, valueClass).setStoreByValue(false).setExpiryPolicyFactory(TouchedExpiryPolicy.factoryOf(Duration.ONE_HOUR)));
        }
        return cache;
    }

    private void checkInitilized() {
        if (isNull(manager)) {
            throw new IllegalStateException("CacheFactory not initialized. Call initialize method before use it");
        }
    }

    private static final class Singleton {
        private static final CacheFactory INSTANCE = new CacheFactory();
    }

    public static CacheFactory getInstance() {
        return  Singleton.INSTANCE;
    }

}
