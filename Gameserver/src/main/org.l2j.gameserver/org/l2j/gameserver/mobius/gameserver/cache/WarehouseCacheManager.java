package org.l2j.gameserver.mobius.gameserver.cache;

import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author -Nemesiss-
 */
public class WarehouseCacheManager
{
    final Map<L2PcInstance, Long> _cachedWh = new ConcurrentHashMap<>();
    final long _cacheTime = Config.WAREHOUSE_CACHE_TIME * 60000;

    protected WarehouseCacheManager()
    {
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new CacheScheduler(), 120000, 60000);
    }

    public void addCacheTask(L2PcInstance pc)
    {
        _cachedWh.put(pc, System.currentTimeMillis());
    }

    public void remCacheTask(L2PcInstance pc)
    {
        _cachedWh.remove(pc);
    }

    private class CacheScheduler implements Runnable
    {
        public CacheScheduler()
        {
        }

        @Override
        public void run()
        {
            final long cTime = System.currentTimeMillis();
            for (L2PcInstance pc : _cachedWh.keySet())
            {
                if ((cTime - _cachedWh.get(pc)) > _cacheTime)
                {
                    pc.clearWarehouse();
                    _cachedWh.remove(pc);
                }
            }
        }
    }

    public static WarehouseCacheManager getInstance()
    {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder
    {
        protected static final WarehouseCacheManager _instance = new WarehouseCacheManager();
    }
}
