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
package org.l2j.gameserver.model;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.OnCreatureSee;
import org.l2j.gameserver.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Predicate;

/**
 * @author UnAfraid
 */
public class CreatureContainer {
    private final Set<Integer> _seen = ConcurrentHashMap.newKeySet();
    private final Creature _owner;
    private final int _range;
    private ScheduledFuture<?> _task;
    private Predicate<Creature> _condition = null;

    public CreatureContainer(Creature owner, int range, Predicate<Creature> condition) {
        _owner = owner;
        _range = range;
        _condition = condition;
        start();
    }

    public Creature getOwner() {
        return _owner;
    }

    public int getRange() {
        return _range;
    }

    /**
     * Starts the task that scans for new creatures
     */
    public void start() {
        if ((_task == null) || _task.isDone()) {
            _task = ThreadPool.scheduleAtFixedRate(this::update, 1000, 1000);
        }
    }

    /**
     * @return {@code false} if the task could not be cancelled, typically because it has already completed normally; {@code true} otherwise
     */
    public boolean stop() {
        return (_task != null) && !_task.isDone() && _task.cancel(false);
    }

    /**
     * Resets the creatures container, all previously seen creature will be discarded and next time update method is called will notify for each creature that owner sees!
     */
    public void reset() {
        _seen.clear();
    }

    /**
     * Scans around the npc and notifies about new creature owner seen
     */
    private void update() {
        final Set<Integer> verified = new HashSet<>();
        World.getInstance().forEachVisibleObjectInRange(_owner, Creature.class, _range, creature ->
        {
            if ((_condition == null) || _condition.test(creature)) {
                if (_seen.add(creature.getObjectId())) {
                    EventDispatcher.getInstance().notifyEventAsync(new OnCreatureSee(_owner, creature), _owner);
                }
                verified.add(creature.getObjectId());
            }
        });

        _seen.retainAll(verified);
    }
}
