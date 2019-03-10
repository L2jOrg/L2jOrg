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
package org.l2j.gameserver.mobius.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.events.EventType;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.IBaseEvent;

/**
 * An instantly executed event when L2Character is killed by L2Character.
 *
 * @author UnAfraid
 */
public class OnNpcCreatureSee implements IBaseEvent {
    private final L2Npc _npc;
    private final L2Character _creature;
    private final boolean _isSummon;

    public OnNpcCreatureSee(L2Npc npc, L2Character creature, boolean isSummon) {
        _npc = npc;
        _creature = creature;
        _isSummon = isSummon;
    }

    public final L2Npc getNpc() {
        return _npc;
    }

    public final L2Character getCreature() {
        return _creature;
    }

    public boolean isSummon() {
        return _isSummon;
    }

    @Override
    public EventType getType() {
        return EventType.ON_NPC_CREATURE_SEE;
    }
}