/*
 * Copyright © 2019-2021 L2JOrg
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
 * @author JoeAlisson
 */
public final class OnPlayerTutorialEvent implements IBaseEvent {
    private final Player player;
    private final int event;

    public OnPlayerTutorialEvent(Player activeChar, int event) {
        player = activeChar;
        this.event = event;
    }

    public Player getPlayer() {
        return player;
    }

    public int getEventId() {
        return event;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_TUTORIAL_EVENT;
    }
}
