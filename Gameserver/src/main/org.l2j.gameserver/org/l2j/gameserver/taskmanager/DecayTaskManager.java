package org.l2j.gameserver.taskmanager;

import org.l2j.gameserver.Config;
import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.L2Attackable;
import org.l2j.gameserver.model.actor.templates.L2NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * @author NosBit
 */
public final class DecayTaskManager {
    protected static final Logger LOGGER = LoggerFactory.getLogger(DecayTaskManager.class);

    protected final Map<Creature, ScheduledFuture<?>> _decayTasks = new ConcurrentHashMap<>();

    private DecayTaskManager() {
    }

    /**
     * Adds a decay task for the specified character.<br>
     * <br>
     * If the decay task already exists it cancels it and re-adds it.
     *
     * @param character the character
     */
    public void add(Creature character) {
        if (character == null) {
            return;
        }

        long delay;
        if (character.getTemplate() instanceof L2NpcTemplate) {
            delay = ((L2NpcTemplate) character.getTemplate()).getCorpseTime();
        } else {
            delay = Config.DEFAULT_CORPSE_TIME;
        }

        if (character.isAttackable() && (((L2Attackable) character).isSpoiled() || ((L2Attackable) character).isSeeded())) {
            delay += Config.SPOILED_CORPSE_EXTEND_TIME;
        }

        // Remove entries that became null.
        _decayTasks.entrySet().removeIf(Objects::isNull);

        try {
            _decayTasks.putIfAbsent(character, ThreadPoolManager.getInstance().schedule(new DecayTask(character), delay * 1000));
        } catch (Exception e) {
            LOGGER.warn("DecayTaskManager add " + character + " caused [" + e.getMessage() + "] exception.");
        }
    }

    /**
     * Cancels the decay task of the specified character.
     *
     * @param character the character
     */
    public void cancel(Creature character) {
        final ScheduledFuture<?> decayTask = _decayTasks.remove(character);
        if (decayTask != null) {
            decayTask.cancel(false);
        }
    }

    /**
     * Gets the remaining time of the specified character's decay task.
     *
     * @param character the character
     * @return if a decay task exists the remaining time, {@code Long.MAX_VALUE} otherwise
     */
    public long getRemainingTime(Creature character) {
        final ScheduledFuture<?> decayTask = _decayTasks.get(character);
        if (decayTask != null) {
            return decayTask.getDelay(TimeUnit.MILLISECONDS);
        }

        return Long.MAX_VALUE;
    }

    @Override
    public String toString() {
        final StringBuilder ret = new StringBuilder();
        ret.append("============= DecayTask Manager Report ============");
        ret.append(Config.EOL);
        ret.append("Tasks count: ");
        ret.append(_decayTasks.size());
        ret.append(Config.EOL);
        ret.append("Tasks dump:");
        ret.append(Config.EOL);

        for (Entry<Creature, ScheduledFuture<?>> entry : _decayTasks.entrySet()) {
            ret.append("Class/Name: ");
            ret.append(entry.getKey().getClass().getSimpleName());
            ret.append('/');
            ret.append(entry.getKey().getName());
            ret.append(" decay timer: ");
            ret.append(entry.getValue().getDelay(TimeUnit.MILLISECONDS));
            ret.append(Config.EOL);
        }

        return ret.toString();
    }

    public static DecayTaskManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final DecayTaskManager INSTANCE = new DecayTaskManager();
    }

    private class DecayTask implements Runnable {

        private final Creature _character;

        protected DecayTask(Creature character) {
            _character = character;
        }

        @Override
        public void run() {
            _decayTasks.remove(_character);
            _character.onDecay();
        }
    }
}
