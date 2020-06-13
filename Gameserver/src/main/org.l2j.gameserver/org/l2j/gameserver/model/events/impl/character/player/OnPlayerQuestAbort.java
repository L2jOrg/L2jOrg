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
package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author Sdw
 */
public class OnPlayerQuestAbort implements IBaseEvent {
    private final Player _activeChar;
    private final int _questId;

    public OnPlayerQuestAbort(Player activeChar, int questId) {
        _activeChar = activeChar;
        _questId = questId;
    }

    public final Player getActiveChar() {
        return _activeChar;
    }

    public final int getQuestId() {
        return _questId;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_QUEST_ABORT;
    }
}
