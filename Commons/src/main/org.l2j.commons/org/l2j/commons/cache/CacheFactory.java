package org.l2j.commons.cache;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import static java.util.Objects.isNull;

public class CacheFactory {

    private static CacheFactory instance;
    private CacheManager manager;

    private CacheFactory() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        manager = cachingProvider.getCacheManager();
    }

    public static CacheFactory getInstance() {
        if(isNull(instance)) {
            instance = new CacheFactory();
        }
        return  instance;
    }

    public <K, V> Cache<K, V> getCache(String alias) {
        return manager.getCache(alias);
    }

    public <K, V> Cache<K, V> getCache(String alias, Class<K> keyClass, Class<V> valueClass) {
        return manager.getCache(alias, keyClass, valueClass);
    }

}
