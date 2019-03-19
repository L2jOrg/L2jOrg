package org.l2j.gameserver.instancemanager;

/**
 * @author Mobius
 */
public final class EventShrineManager {
    private static boolean ENABLE_SHRINES = false;

    private EventShrineManager() { }

    public static EventShrineManager getInstance() {
        return Singleton.INSTANCE;
    }

    public boolean areShrinesEnabled() {
        return ENABLE_SHRINES;
    }

    public void setEnabled(boolean enabled) {
        ENABLE_SHRINES = enabled;
    }

    private static class Singleton {
        private static final EventShrineManager INSTANCE = new EventShrineManager();
    }
}
