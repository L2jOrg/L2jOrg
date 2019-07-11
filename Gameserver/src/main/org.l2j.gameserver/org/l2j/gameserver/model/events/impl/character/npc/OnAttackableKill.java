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
package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * An instantly executed event when Attackable is killed by Player.
 *
 * @author UnAfraid
 */
public class OnAttackableKill implements IBaseEvent {
    private final Player _attacker;
    private final Attackable _target;
    private final boolean _isSummon;

    public OnAttackableKill(Player attacker, Attackable target, boolean isSummon) {
        _attacker = attacker;
        _target = target;
        _isSummon = isSummon;
    }

    public final Player getAttacker() {
        return _attacker;
    }

    public final Attackable getTarget() {
        return _target;
    }

    public final boolean isSummon() {
        return _isSummon;
    }

    @Override
    public EventType getType() {
        return EventType.ON_ATTACKABLE_KILL;
    }
}