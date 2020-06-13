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
package org.l2j.gameserver.taskmanager;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import static org.l2j.gameserver.util.GameUtils.isAttackable;


/**
 * @author NosBit
 */
public final class DecayTaskManager {
    protected static final Logger LOGGER = LoggerFactory.getLogger(DecayTaskManager.class);

    private static final Map<Creature, Long> DECAY_SCHEDULES = new ConcurrentHashMap<>();

    private DecayTaskManager() {
        ThreadPool.scheduleAtFixedRate(() ->
        {
            final long time = System.currentTimeMillis();
            for (Entry<Creature, Long> entry : DECAY_SCHEDULES.entrySet())
            {
                if (time > entry.getValue())
                {
                    final Creature creature = entry.getKey();
                    DECAY_SCHEDULES.remove(creature);
                    creature.onDecay();
                }
            }
        }, 0, 1000);
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
        if (character.getTemplate() instanceof NpcTemplate) {
            delay = ((NpcTemplate) character.getTemplate()).getCorpseTime();
        } else {
            delay = Config.DEFAULT_CORPSE_TIME;
        }

        if (isAttackable(character) && (((Attackable) character).isSpoiled() || ((Attackable) character).isSeeded())) {
            delay += Config.SPOILED_CORPSE_EXTEND_TIME;
        }

		// Add to decay schedules.
		DECAY_SCHEDULES.put(character, System.currentTimeMillis() + (delay * 1000));
    }

    /**
     * Cancels the decay task of the specified character.
     *
     * @param creature the character
     */
	public void cancel(Creature creature)
	{
		DECAY_SCHEDULES.remove(creature);
    }

    /**
     * Gets the remaining time of the specified character's decay task.
     *
     * @param creature the character
     * @return if a decay task exists the remaining time, {@code Long.MAX_VALUE} otherwise
     */
	public long getRemainingTime(Creature creature)
	{
		final Long time = DECAY_SCHEDULES.get(creature);
		return time != null ? time - System.currentTimeMillis() : Long.MAX_VALUE;
    }

    @Override
    public String toString() {
        final StringBuilder ret = new StringBuilder();
        ret.append("============= DecayTask Manager Report ============");
        ret.append(System.lineSeparator());
        ret.append("Tasks count: ");
		ret.append(DECAY_SCHEDULES.size());
        ret.append(System.lineSeparator());
        ret.append("Tasks dump:");
        ret.append(System.lineSeparator());

		final long time = System.currentTimeMillis();
		for (Entry<Creature, Long> entry : DECAY_SCHEDULES.entrySet())
		{
            ret.append("Class/Name: ");
            ret.append(entry.getKey().getClass().getSimpleName());
            ret.append('/');
            ret.append(entry.getKey().getName());
            ret.append(" decay timer: ");
			ret.append(entry.getValue() - time);
            ret.append(System.lineSeparator());
        }

        return ret.toString();
    }

    public static DecayTaskManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final DecayTaskManager INSTANCE = new DecayTaskManager();
    }
}
