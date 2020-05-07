package org.l2j.gameserver.instancemanager;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.timers.TimerHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author UnAfraid
 */
public class TimersManager {
    private final Map<Integer, List<TimerHolder<?>>> _timers = new ConcurrentHashMap<>();

    private TimersManager() {

    }

    public void registerTimer(TimerHolder<?> timer) {
        final Npc npc = timer.getNpc();
        if (npc != null) {
            final List<TimerHolder<?>> npcTimers = _timers.computeIfAbsent(npc.getObjectId(), key -> new ArrayList<>());
            synchronized (npcTimers) {
                npcTimers.add(timer);
            }
        }

        final Player player = timer.getPlayer();
        if (player != null) {
            final List<TimerHolder<?>> playerTimers = _timers.computeIfAbsent(player.getObjectId(), key -> new ArrayList<>());
            synchronized (playerTimers) {
                playerTimers.add(timer);
            }
        }
    }

    public void cancelTimers(int objectId) {
        final List<TimerHolder<?>> timers = _timers.remove(objectId);
        if (timers != null) {
            synchronized (timers) {
                timers.forEach(TimerHolder::cancelTimer);
            }
        }
    }

    public void unregisterTimer(int objectId, TimerHolder<?> timer) {
        final List<TimerHolder<?>> timers = _timers.get(objectId);
        if (timers != null) {
            synchronized (timers) {
                timers.remove(timer);
            }
        }
    }

    public static TimersManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final TimersManager INSTANCE = new TimersManager();
    }
}
