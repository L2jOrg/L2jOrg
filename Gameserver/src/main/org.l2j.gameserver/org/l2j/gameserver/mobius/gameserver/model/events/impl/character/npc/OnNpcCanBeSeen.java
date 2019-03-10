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

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.events.EventType;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnNpcCanBeSeen implements IBaseEvent {
    private final L2Npc _npc;
    private final L2PcInstance _activeChar;

    public OnNpcCanBeSeen(L2Npc npc, L2PcInstance activeChar) {
        _npc = npc;
        _activeChar = activeChar;
    }

    public L2Npc getNpc() {
        return _npc;
    }

    public L2PcInstance getActiveChar() {
        return _activeChar;
    }

    @Override
    public EventType getType() {
        return EventType.ON_NPC_CAN_BE_SEEN;
    }
}
