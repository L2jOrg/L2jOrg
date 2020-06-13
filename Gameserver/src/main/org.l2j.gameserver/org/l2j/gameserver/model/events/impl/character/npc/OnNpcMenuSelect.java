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
package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author St3eT
 */
public class OnNpcMenuSelect implements IBaseEvent {
    private final Player _activeChar;
    private final Npc _npc;
    private final int _ask;
    private final int _reply;

    /**
     * @param activeChar
     * @param npc
     * @param ask
     * @param reply
     */
    public OnNpcMenuSelect(Player activeChar, Npc npc, int ask, int reply) {
        _activeChar = activeChar;
        _npc = npc;
        _ask = ask;
        _reply = reply;
    }

    public Player getTalker() {
        return _activeChar;
    }

    public Npc getNpc() {
        return _npc;
    }

    public int getAsk() {
        return _ask;
    }

    public int getReply() {
        return _reply;
    }

    @Override
    public EventType getType() {
        return EventType.ON_NPC_MENU_SELECT;
    }
}
