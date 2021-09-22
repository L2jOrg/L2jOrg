/*
 * Copyright © 2019-2021 L2JOrg
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

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public final class CacheFactory {

    private final CacheManager manager;

    private CacheFactory() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        manager = cachingProvider.getCacheManager();
    }

    public <K, V> MutableConfiguration<K, V> build(String cacheName, Class<K> keyClass, Class<V> valueClass) {
        var config = defaultConfiguration(keyClass, valueClass);
        manager.createCache(cacheName, config);
        return config;
    }

    private <K, V> MutableConfiguration<K, V> defaultConfiguration() {
        return new MutableConfiguration<K, V>().setStoreByValue(false).setExpiryPolicyFactory(TouchedExpiryPolicy.factoryOf(Duration.THIRTY_MINUTES));
    }

    private <K, V> MutableConfiguration<K, V> defaultConfiguration(Class<K> keyClass, Class<V> valueClass) {
        return new MutableConfiguration<K, V>().setTypes(keyClass, valueClass).setStoreByValue(false).setExpiryPolicyFactory(TouchedExpiryPolicy.factoryOf(Duration.THIRTY_MINUTES));
    }

    public <K, V> Cache<K, V> getCache(String alias) {
        Cache<K, V> cache = manager.getCache(alias);
        if(isNull(cache)) {
            cache =  manager.createCache(alias, defaultConfiguration());
        }
        return cache;
    }

    public <K, V> Cache<K, V> getCache(String alias, Class<K> keyClass, Class<V> valueClass) {
        Cache<K, V> cache = manager.getCache(alias, keyClass, valueClass);
        if(isNull(cache)) {
            cache = manager.createCache(alias, defaultConfiguration(keyClass, valueClass));
        }
        return cache;
    }

    private static final class Singleton {
        private static final CacheFactory INSTANCE = new CacheFactory();
    }

    public static CacheFactory getInstance() {
        return  Singleton.INSTANCE;
    }

}
