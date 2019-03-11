/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.actor.tasks.attackable;

import org.l2j.gameserver.model.actor.L2Attackable;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.quest.Quest;

/**
 * @author xban1x
 */
public final class OnKillNotifyTask implements Runnable {
    private final L2Attackable _attackable;
    private final Quest _quest;
    private final L2PcInstance _killer;
    private final boolean _isSummon;

    public OnKillNotifyTask(L2Attackable attackable, Quest quest, L2PcInstance killer, boolean isSummon) {
        _attackable = attackable;
        _quest = quest;
        _killer = killer;
        _isSummon = isSummon;
    }

    @Override
    public void run() {
        if ((_quest != null) && (_attackable != null) && (_killer != null)) {
            _quest.notifyKill(_attackable, _killer, _isSummon);
        }
    }
}
