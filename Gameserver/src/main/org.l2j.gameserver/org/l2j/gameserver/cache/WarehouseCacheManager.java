package org.l2j.gameserver.cache;

import org.l2j.gameserver.Config;
import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author -Nemesiss-
 */
public class WarehouseCacheManager {
    private final Map<L2PcInstance, Long> _cachedWh = new ConcurrentHashMap<>();
    private final long _cacheTime = Config.WAREHOUSE_CACHE_TIME * 60000;

    private WarehouseCacheManager() {
        ThreadPoolManager.scheduleAtFixedRate(new CacheScheduler(), 120000, 60000);
    }

    public void addCacheTask(L2PcInstance pc) {
        _cachedWh.put(pc, System.currentTimeMillis());
    }

    public void remCacheTask(L2PcInstance pc) {
        _cachedWh.remove(pc);
    }

    public static WarehouseCacheManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {

        private static final WarehouseCacheManager INSTANCE = new WarehouseCacheManager();
    }

    private class CacheScheduler implements Runnable {
        public CacheScheduler() {
        }

        @Override
        public void run() {
            final long cTime = System.currentTimeMillis();
            for (L2PcInstance pc : _cachedWh.keySet()) {
                if ((cTime - _cachedWh.get(pc)) > _cacheTime) {
                    pc.clearWarehouse();
                    _cachedWh.remove(pc);
                }
            }
        }
    }
}
