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

import org.l2j.gameserver.data.database.data.CropProcure;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.HashMap;
import java.util.Map;

public class SellListProcure extends ServerPacket {
    private final long _money;
    private final Map<Item, Long> _sellList = new HashMap<>();

    public SellListProcure(Player player, int castleId) {
        _money = player.getAdena();
        for (CropProcure c : CastleManorManager.getInstance().getCropProcure(castleId, false)) {
            final Item item = player.getInventory().getItemByItemId(c.getSeedId());
            if ((item != null) && (c.getAmount() > 0)) {
                _sellList.put(item, c.getAmount());
            }
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SELL_LIST_PROCURE);

        writeLong(_money); // money
        writeInt(0x00); // lease ?
        writeShort((short) _sellList.size()); // list size

        for (Item item : _sellList.keySet()) {
            writeShort((short) item.getTemplate().getType1());
            writeInt(item.getObjectId());
            writeInt(item.getDisplayId());
            writeLong(_sellList.get(item)); // count
            writeShort((short) item.getTemplate().getType2());
            writeShort((short) 0); // unknown
            writeLong(0); // price, u shouldnt get any adena for crops, only raw materials
        }
    }

}
