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

/**
 * @author daemon
 */
public class TradeUpdate extends AbstractItemPacket {
    private final int sendType;
    private final TradeItem item;
    private final long newCount;
    private final long count;

    public TradeUpdate(int sendType, Player player, TradeItem item, long count) {
        this.sendType = sendType;
        this.count = count;
        this.item = item;
        newCount = player == null ? 0 : player.getInventory().getItemByObjectId(item.getObjectId()).getCount() - item.getCount();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.TRADE_UPDATE);
        writeByte((byte) sendType);
        writeInt(0x01);
        if (sendType == 2) {
            writeInt(0x01);
            writeShort((short) ((newCount > 0) && item.getItem().isStackable() ? 3 : 2));
            writeItem(item, count);
        }
    }

}
