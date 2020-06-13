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
package org.l2j.gameserver.model.events.impl.item;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.item.instance.Item;

/**
 * @author UnAfraid
 */
public class OnItemBypassEvent implements IBaseEvent {
    private final Item _item;
    private final Player _activeChar;
    private final String _event;

    public OnItemBypassEvent(Item item, Player activeChar, String event) {
        _item = item;
        _activeChar = activeChar;
        _event = event;
    }

    public Item getItem() {
        return _item;
    }

    public Player getActiveChar() {
        return _activeChar;
    }

    public String getEvent() {
        return _event;
    }

    @Override
    public EventType getType() {
        return EventType.ON_ITEM_BYPASS_EVENT;
    }

}
