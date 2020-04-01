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

/**
 * @author St3eT
 */
public final class OnPlayerPressTutorialMark implements IBaseEvent {
    private final Player _activeChar;
    private final int _markId;

    public OnPlayerPressTutorialMark(Player activeChar, int markId) {
        _activeChar = activeChar;
        _markId = markId;
    }

    public Player getPlayer() {
        return _activeChar;
    }

    public int getMarkId() {
        return _markId;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_PRESS_TUTORIAL_MARK;
    }
}
