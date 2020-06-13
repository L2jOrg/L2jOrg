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
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

import java.util.concurrent.ScheduledFuture;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 */
public class MpRewardTask {
    private int _count;
    private final double _value;
    private final ScheduledFuture<?> _task;
    private final Creature _creature;

    public MpRewardTask(Creature creature, Npc npc) {
        final NpcTemplate template = npc.getTemplate();
        _creature = creature;
        _count = template.getMpRewardTicks();
        _value = calculateBaseValue(npc, creature);
        _task = ThreadPool.scheduleAtFixedRate(this::run, Config.EFFECT_TICK_RATIO, Config.EFFECT_TICK_RATIO);
    }

    /**
     * @param npc
     * @param creature
     * @return
     */
    private double calculateBaseValue(Npc npc, Creature creature) {
        final NpcTemplate template = npc.getTemplate();
        switch (template.getMpRewardType()) {
            case PER: {
                return (creature.getMaxMp() * (template.getMpRewardValue() / 100d)) / template.getMpRewardTicks();
            }
        }
        return template.getMpRewardValue() / (double) template.getMpRewardTicks();
    }

    private void run() {
        if ((--_count <= 0) || (isPlayer(_creature) && !_creature.getActingPlayer().isOnline())) {
            _task.cancel(false);
            return;
        }

        _creature.setCurrentMp(_creature.getCurrentMp() + _value);
    }
}
