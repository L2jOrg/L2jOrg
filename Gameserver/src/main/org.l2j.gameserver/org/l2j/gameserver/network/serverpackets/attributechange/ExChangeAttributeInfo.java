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
package org.l2j.gameserver.network.serverpackets.attributechange;

import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mobius
 */
public class ExChangeAttributeInfo extends ServerPacket {
    private static final Map<AttributeType, Byte> ATTRIBUTE_MASKS = new HashMap<>();
    private final int _crystalItemId;
    private int _attributes;
    private int _itemObjId;

    {
        ATTRIBUTE_MASKS.put(AttributeType.FIRE, (byte) 1);
        ATTRIBUTE_MASKS.put(AttributeType.WATER, (byte) 2);
        ATTRIBUTE_MASKS.put(AttributeType.WIND, (byte) 4);
        ATTRIBUTE_MASKS.put(AttributeType.EARTH, (byte) 8);
        ATTRIBUTE_MASKS.put(AttributeType.HOLY, (byte) 16);
        ATTRIBUTE_MASKS.put(AttributeType.DARK, (byte) 32);
    }

    public ExChangeAttributeInfo(int crystalItemId, Item item) {
        _crystalItemId = crystalItemId;
        _attributes = 0;
        for (AttributeType e : AttributeType.ATTRIBUTE_TYPES) {
            if (e != item.getAttackAttributeType()) {
                _attributes |= ATTRIBUTE_MASKS.get(e);
            }
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_CHANGE_ATTRIBUTE_INFO);
        writeInt(_crystalItemId);
        writeInt(_attributes);
        writeInt(_itemObjId);
    }

}