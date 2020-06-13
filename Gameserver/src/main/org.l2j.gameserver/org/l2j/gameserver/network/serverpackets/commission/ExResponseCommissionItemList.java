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
package org.l2j.gameserver.network.serverpackets.commission;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;

import java.util.Collection;

/**
 * @author NosBit
 */
public class ExResponseCommissionItemList extends AbstractItemPacket {
    private final int sendType;
    private final Collection<Item> items;

    public ExResponseCommissionItemList(int sendType, Collection<Item> items) {
        this.sendType = sendType;
        this.items = items;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_RESPONSE_COMMISSION_ITEM_LIST);
        writeByte((byte) sendType);
        if (sendType == 2) {
            writeInt(items.size());
            writeInt(items.size());
            for (Item itemInstance : items) {
                writeItem(itemInstance);
            }
        } else {
            writeInt(0);
            writeInt(0);
        }
    }

}
