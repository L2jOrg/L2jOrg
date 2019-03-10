/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.instancemanager;

/**
 * @author Mobius
 */
public class PlayerCountManager {
    private static volatile int connectedCount = 0;
    private static volatile int maxConnectedCount = 0;
    private static volatile int offlineTradeCount = 0;

    protected PlayerCountManager() {
    }

    public static PlayerCountManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public int getConnectedCount() {
        return connectedCount;
    }

    public int getMaxConnectedCount() {
        return maxConnectedCount;
    }

    public int getOfflineTradeCount() {
        return offlineTradeCount;
    }

    public synchronized void incConnectedCount() {
        connectedCount++;
        maxConnectedCount = Math.max(maxConnectedCount, connectedCount);
    }

    public void decConnectedCount() {
        connectedCount--;
    }

    public void incOfflineTradeCount() {
        offlineTradeCount++;
    }

    public synchronized void decOfflineTradeCount() {
        offlineTradeCount = Math.max(0, offlineTradeCount - 1);
    }

    private static class SingletonHolder {
        protected static final PlayerCountManager INSTANCE = new PlayerCountManager();
    }
}
