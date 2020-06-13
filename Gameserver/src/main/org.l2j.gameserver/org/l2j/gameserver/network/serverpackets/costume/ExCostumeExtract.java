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
package org.l2j.gameserver.network.serverpackets.costume;

import org.l2j.gameserver.data.database.data.CostumeData;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExCostumeExtract extends ServerPacket {

    private int costumeId;
    private boolean success;
    private long amount;
    private int extractedItem;
    private long totalAmount;

    private ExCostumeExtract() {
    }

    public static ExCostumeExtract failed(int costumeId) {
        var packet = new ExCostumeExtract();
        packet.costumeId = costumeId;
        return packet;
    }

    public static ExCostumeExtract success(CostumeData costume, int extractItem, long amount) {
        var packet = new ExCostumeExtract();
        packet.costumeId = costume.getId();
        packet.success = true;
        packet.extractedItem = extractItem;
        packet.amount = amount;
        packet.totalAmount = costume.getAmount();
        return packet;
    }

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerExPacketId.EX_COSTUME_EXTRACT);
        writeByte(success);
        writeInt(costumeId);
        writeLong(amount);
        writeInt(extractedItem);
        writeLong(amount);
        writeLong(totalAmount);
    }
}
