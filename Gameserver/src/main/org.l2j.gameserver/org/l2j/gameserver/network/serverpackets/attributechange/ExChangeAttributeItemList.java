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
package org.l2j.gameserver.network.serverpackets.attributechange;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;

/**
 * @author Mobius
 */
public class ExChangeAttributeItemList extends AbstractItemPacket {
    private final ItemInfo[] _itemsList;
    private final int _itemId;

    public ExChangeAttributeItemList(int itemId, ItemInfo[] itemsList) {
        _itemId = itemId;
        _itemsList = itemsList;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_CHANGE_ATTRIBUTE_ITEM_LIST, buffer );
        buffer.writeInt(_itemId);
        buffer.writeInt(_itemsList.length);
        for (ItemInfo item : _itemsList) {
            writeItem(item, buffer);
        }
    }

}
