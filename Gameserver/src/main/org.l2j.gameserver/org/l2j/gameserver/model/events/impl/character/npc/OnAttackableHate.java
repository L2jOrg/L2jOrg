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

import org.l2j.gameserver.model.actor.L2Attackable;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnAttackableHate implements IBaseEvent {
    private final L2Attackable _npc;
    private final L2PcInstance _activeChar;
    private final boolean _isSummon;

    public OnAttackableHate(L2Attackable npc, L2PcInstance activeChar, boolean isSummon) {
        _npc = npc;
        _activeChar = activeChar;
        _isSummon = isSummon;
    }

    public final L2Attackable getNpc() {
        return _npc;
    }

    public final L2PcInstance getActiveChar() {
        return _activeChar;
    }

    public boolean isSummon() {
        return _isSummon;
    }

    @Override
    public EventType getType() {
        return EventType.ON_NPC_HATE;
    }
}