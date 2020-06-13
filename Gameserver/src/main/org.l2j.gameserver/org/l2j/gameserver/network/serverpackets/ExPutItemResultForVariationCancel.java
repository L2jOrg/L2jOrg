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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

public class ExPutItemResultForVariationCancel extends ServerPacket {
    private final int _itemObjId;
    private final int _itemId;
    private final int _itemAug1;
    private final int _itemAug2;
    private final long _price;

    public ExPutItemResultForVariationCancel(Item item, long price) {
        _itemObjId = item.getObjectId();
        _itemId = item.getDisplayId();
        _price = price;
        _itemAug1 = item.getAugmentation().getOption1Id();
        _itemAug2 = item.getAugmentation().getOption2Id();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_PUT_ITEM_RESULT_FOR_VARIATION_CANCEL);

        writeInt(_itemObjId);
        writeInt(_itemId);
        writeInt(_itemAug1);
        writeInt(_itemAug2);
        writeLong(_price);
        writeInt(0x01);
    }

}
