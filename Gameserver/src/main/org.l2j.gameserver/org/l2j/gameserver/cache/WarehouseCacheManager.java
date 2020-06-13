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
package org.l2j.gameserver.cache;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author -Nemesiss-
 */
public class WarehouseCacheManager {
    private final Map<Player, Long> _cachedWh = new ConcurrentHashMap<>();
    private final long _cacheTime = Config.WAREHOUSE_CACHE_TIME * 60000;

    private WarehouseCacheManager() {
        ThreadPool.scheduleAtFixedRate(new CacheScheduler(), 120000, 60000);
    }

    public void addCacheTask(Player pc) {
        _cachedWh.put(pc, System.currentTimeMillis());
    }

    public void remCacheTask(Player pc) {
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
            for (Player pc : _cachedWh.keySet()) {
                if ((cTime - _cachedWh.get(pc)) > _cacheTime) {
                    pc.clearWarehouse();
                    _cachedWh.remove(pc);
                }
            }
        }
    }
}
