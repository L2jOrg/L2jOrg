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

import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

public class GMViewItemList extends AbstractItemPacket {
    private final int sendType;
    private final Collection<Item> items;
    private final int _limit;
    private final String playerName;

    public GMViewItemList(int sendType, Player cha) {
        this.sendType = sendType;
        playerName = cha.getName();
        _limit = cha.getInventoryLimit();
        items = cha.getInventory().getItems();
    }

    public GMViewItemList(int sendType, Pet cha) {
        this.sendType = sendType;
        playerName = cha.getName();
        _limit = cha.getInventoryLimit();
        items = cha.getInventory().getItems();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.GM_VIEW_ITEMLIST);
        writeByte((byte) sendType);
        if (sendType == 2) {
            writeInt(items.size());
        } else {
            writeString(playerName);
            writeInt(_limit); // inventory limit
        }
        writeInt(items.size());
        for (Item item : items) {
            writeItem(item);
        }
    }

}
