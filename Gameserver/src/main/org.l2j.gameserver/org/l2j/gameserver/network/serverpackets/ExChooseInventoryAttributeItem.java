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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.Elementals;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kerberos
 */
public class ExChooseInventoryAttributeItem extends ServerPacket {
    private final int _itemId;
    private final long _count;
    private final AttributeType _atribute;
    private final int _level;
    private final Set<Integer> _items = new HashSet<>();

    public ExChooseInventoryAttributeItem(Player activeChar, Item stone) {
        _itemId = stone.getDisplayId();
        _count = stone.getCount();
        _atribute = AttributeType.findByClientId(Elementals.getItemElement(_itemId));
        if ((_atribute == AttributeType.NONE)) {
            throw new IllegalArgumentException("Undefined Atribute item: " + stone);
        }
        _level = Elementals.getMaxElementLevel(_itemId);
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_CHOOSE_INVENTORY_ATTRIBUTE_ITEM, buffer );

        buffer.writeInt(_itemId);
        buffer.writeLong(_count);
        buffer.writeInt(_atribute == AttributeType.FIRE ? 1 : 0); // Fire
        buffer.writeInt(_atribute == AttributeType.WATER ? 1 : 0); // Water
        buffer.writeInt(_atribute == AttributeType.WIND ? 1 : 0); // Wind
        buffer.writeInt(_atribute == AttributeType.EARTH ? 1 : 0); // Earth
        buffer.writeInt(_atribute == AttributeType.HOLY ? 1 : 0); // Holy
        buffer.writeInt(_atribute == AttributeType.DARK ? 1 : 0); // Unholy
        buffer.writeInt(_level); // Item max attribute level
        buffer.writeInt(_items.size());
        _items.forEach(buffer::writeInt);
    }

}
