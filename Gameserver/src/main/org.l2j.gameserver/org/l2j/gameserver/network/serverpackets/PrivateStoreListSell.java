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

import org.l2j.gameserver.instancemanager.SellBuffsManager;
import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class PrivateStoreListSell extends AbstractItemPacket {
    private final Player _player;
    private final Player _seller;

    public PrivateStoreListSell(Player player, Player seller) {
        _player = player;
        _seller = seller;
    }

    @Override
    public void writeImpl(GameClient client) {
        if (_seller.isSellingBuffs()) {
            SellBuffsManager.getInstance().sendBuffMenu(_player, _seller, 0);
        } else {
            writeId(ServerPacketId.PRIVATE_STORE_LIST);

            writeInt(_seller.getObjectId());
            writeInt(_seller.getSellList().isPackaged() ? 1 : 0);
            writeLong(_player.getAdena());
            writeInt(0x00);
            writeInt(_seller.getSellList().getItems().length);
            for (TradeItem item : _seller.getSellList().getItems()) {
                writeItem(item);
                writeLong(item.getPrice());
                writeLong(item.getItem().getReferencePrice() * 2);
            }
        }
    }

}
