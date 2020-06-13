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
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.network.serverpackets.AutoAttackStop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import static org.l2j.gameserver.util.GameUtils.isPlayer;
import static org.l2j.gameserver.util.GameUtils.isSummon;


/**
 * Attack stance task manager.
 *
 * @author Luca Baldi, Zoey76
 */
public class AttackStanceTaskManager {
    public static final long COMBAT_TIME = 15_000;
    protected static final Logger LOGGER = LoggerFactory.getLogger(AttackStanceTaskManager.class);
    protected static final Map<Creature, Long> _attackStanceTasks = new ConcurrentHashMap<>();

    /**
     * Instantiates a new attack stance task manager.
     */
    private AttackStanceTaskManager() {
        ThreadPool.scheduleAtFixedRate(new FightModeScheduler(), 0, 1000);
    }

    /**
     * Adds the attack stance task.
     *
     * @param actor the actor
     */
    public void addAttackStanceTask(Creature actor) {
        if (actor != null) {
            _attackStanceTasks.put(actor, System.currentTimeMillis());
        }
    }

    /**
     * Removes the attack stance task.
     *
     * @param actor the actor
     */
    public void removeAttackStanceTask(Creature actor) {
        if (actor != null) {
            if (isSummon(actor)) {
                actor = actor.getActingPlayer();
            }
            _attackStanceTasks.remove(actor);
        }
    }

    /**
     * Checks for attack stance task.<br>
     *
     * @param actor the actor
     * @return {@code true} if the character has an attack stance task, {@code false} otherwise
     */
    public boolean hasAttackStanceTask(Creature actor) {
        if (actor != null) {
            if (isSummon(actor)) {
                actor = actor.getActingPlayer();
            }
            return _attackStanceTasks.containsKey(actor);
        }
        return false;
    }

    public static AttackStanceTaskManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final AttackStanceTaskManager INSTANCE = new AttackStanceTaskManager();
    }

    protected class FightModeScheduler implements Runnable {
        @Override
        public void run() {
            final long current = System.currentTimeMillis();
            try {
                final Iterator<Entry<Creature, Long>> iter = _attackStanceTasks.entrySet().iterator();
                Entry<Creature, Long> e;
                Creature actor;
                while (iter.hasNext()) {
                    e = iter.next();
                    if ((current - e.getValue()) > COMBAT_TIME) {
                        actor = e.getKey();
                        if (actor != null) {
                            actor.broadcastPacket(new AutoAttackStop(actor.getObjectId()));
                            actor.getAI().setAutoAttacking(false);
                            if (isPlayer(actor) && actor.hasSummon()) {
                                final Summon pet = actor.getPet();
                                if (pet != null) {
                                    pet.broadcastPacket(new AutoAttackStop(pet.getObjectId()));
                                }
                                actor.getServitors().values().forEach(s -> s.broadcastPacket(new AutoAttackStop(s.getObjectId())));
                            }
                        }
                        iter.remove();
                    }
                }
            } catch (Exception e) {
                // Unless caught here, players remain in attack positions.
                LOGGER.warn("Error in FightModeScheduler: " + e.getMessage(), e);
            }
        }
    }
}
