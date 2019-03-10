package org.l2j.gameserver.mobius.gameserver.taskmanager;

import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Attackable;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.templates.L2NpcTemplate;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author NosBit
 */
public final class DecayTaskManager {
    protected static final Logger LOGGER = Logger.getLogger(DecayTaskManager.class.getName());

    protected final Map<L2Character, ScheduledFuture<?>> _decayTasks = new ConcurrentHashMap<>();

    public static DecayTaskManager getInstance() {
        return SingletonHolder._instance;
    }

    /**
     * Adds a decay task for the specified character.<br>
     * <br>
     * If the decay task already exists it cancels it and re-adds it.
     *
     * @param character the character
     */
    public void add(L2Character character) {
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
            LOGGER.warning("DecayTaskManager add " + character + " caused [" + e.getMessage() + "] exception.");
        }
    }

    /**
     * Cancels the decay task of the specified character.
     *
     * @param character the character
     */
    public void cancel(L2Character character) {
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
    public long getRemainingTime(L2Character character) {
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

        for (Entry<L2Character, ScheduledFuture<?>> entry : _decayTasks.entrySet()) {
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

    private static class SingletonHolder {
        protected static final DecayTaskManager _instance = new DecayTaskManager();
    }

    private class DecayTask implements Runnable {
        private final L2Character _character;

        protected DecayTask(L2Character character) {
            _character = character;
        }

        @Override
        public void run() {
            _decayTasks.remove(_character);
            _character.onDecay();
        }
    }
}
