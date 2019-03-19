package org.l2j.gameserver.instancemanager;

/**
 * @author Mobius
 */
public class PlayerCountManager {
    private static volatile int connectedCount = 0;
    private static volatile int maxConnectedCount = 0;
    private static volatile int offlineTradeCount = 0;

    private PlayerCountManager() {
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

    public static PlayerCountManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final PlayerCountManager INSTANCE = new PlayerCountManager();
    }
}