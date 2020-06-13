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

import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

/**
 * This class ...
 *
 * @version $Revision: 1.7.2.2.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class PrivateStoreListBuy extends AbstractItemPacket {
    private final int _objId;
    private final long _playerAdena;
    private final Collection<TradeItem> _items;

    public PrivateStoreListBuy(Player player, Player storePlayer) {
        _objId = storePlayer.getObjectId();
        _playerAdena = player.getAdena();
        storePlayer.getSellList().updateItems(); // Update SellList for case inventory content has changed
        _items = storePlayer.getBuyList().getAvailableItems(player.getInventory());
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PRIVATE_STORE_BUY_LIST);

        writeInt(_objId);
        writeLong(_playerAdena);
        writeInt(0x00); // Viewer's item count?
        writeInt(_items.size());

        int slotNumber = 0;
        for (TradeItem item : _items) {
            slotNumber++;
            writeItem(item);
            writeInt(slotNumber); // Slot in shop
            writeLong(item.getPrice());
            writeLong(item.getItem().getReferencePrice() * 2);
            writeLong(item.getStoreCount());
        }
    }

}
