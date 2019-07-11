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
package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.items.Henna;

/**
 * @author UnAfraid
 */
public class OnPlayerHennaAdd implements IBaseEvent {
    private final Player _activeChar;
    private final Henna _henna;

    public OnPlayerHennaAdd(Player activeChar, Henna henna) {
        _activeChar = activeChar;
        _henna = henna;
    }

    public Player getActiveChar() {
        return _activeChar;
    }

    public Henna getHenna() {
        return _henna;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_HENNA_ADD;
    }
}
