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

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.instance.Trap;

/**
 * Trap trigger task.
 *
 * @author Zoey76
 */
public class TrapTriggerTask implements Runnable {
    private final Trap _trap;

    public TrapTriggerTask(Trap trap) {
        _trap = trap;
    }

    @Override
    public void run() {
        try {
            _trap.doCast(_trap.getSkill());
            ThreadPool.schedule(new TrapUnsummonTask(_trap), _trap.getSkill().getHitTime() + 300);
        } catch (Exception e) {
            _trap.unSummon();
        }
    }
}
