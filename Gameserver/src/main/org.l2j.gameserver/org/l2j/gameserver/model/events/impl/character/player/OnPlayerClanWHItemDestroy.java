/*
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
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.model.item.instance.Item;

/**
 * @author UnAfraid
 */
public class OnPlayerClanWHItemDestroy implements IBaseEvent {
    private final String _process;
    private final Player _activeChar;
    private final Item _item;
    private final long _count;
    private final ItemContainer _container;

    public OnPlayerClanWHItemDestroy(String process, Player activeChar, Item item, long count, ItemContainer container) {
        _process = process;
        _activeChar = activeChar;
        _item = item;
        _count = count;
        _container = container;
    }

    public String getProcess() {
        return _process;
    }

    public Player getActiveChar() {
        return _activeChar;
    }

    public Item getItem() {
        return _item;
    }

    public long getCount() {
        return _count;
    }

    public ItemContainer getContainer() {
        return _container;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_CLAN_WH_ITEM_DESTROY;
    }
}
