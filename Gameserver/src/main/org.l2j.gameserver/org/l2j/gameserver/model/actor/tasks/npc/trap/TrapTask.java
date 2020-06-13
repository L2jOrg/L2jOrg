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
package org.l2j.gameserver.model.actor.tasks.npc.trap;

import org.l2j.gameserver.model.actor.instance.Trap;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Trap task.
 *
 * @author Zoey76
 */
public class TrapTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrapTask.class);
    private static final int TICK = 1000; // 1s
    private final Trap _trap;

    public TrapTask(Trap trap) {
        _trap = trap;
    }

    @Override
    public void run() {
        try {
            if (!_trap.isTriggered()) {
                if (_trap.hasLifeTime()) {
                    _trap.setRemainingTime(_trap.getRemainingTime() - TICK);
                    if (_trap.getRemainingTime() < (_trap.getLifeTime() - 15000)) {
                        _trap.broadcastPacket(new SocialAction(_trap.getObjectId(), 2));
                    }
                    if (_trap.getRemainingTime() <= 0) {
                        _trap.triggerTrap(_trap);
                        return;
                    }
                }

                if (!_trap.getSkill().getTargetsAffected(_trap, _trap).isEmpty()) {
                    _trap.triggerTrap(_trap);
                }
            }
        } catch (Exception e) {
            LOGGER.error(TrapTask.class.getSimpleName() + ": " + e.getMessage());
            _trap.unSummon();
        }
    }
}
