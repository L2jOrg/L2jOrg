/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.actor.tasks.attackable;

import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;

/**
 * @author xban1x
 */
public final class OnKillNotifyTask implements Runnable {
    private final Attackable _attackable;
    private final Quest _quest;
    private final Player _killer;
    private final boolean _isSummon;

    public OnKillNotifyTask(Attackable attackable, Quest quest, Player killer, boolean isSummon) {
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
