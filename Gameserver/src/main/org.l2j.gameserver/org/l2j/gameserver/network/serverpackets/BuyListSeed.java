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

import org.l2j.gameserver.data.database.data.SeedProduction;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author l3x
 */
public final class BuyListSeed extends ServerPacket {
    private final int _manorId;
    private final long _money;
    private final List<SeedProduction> _list = new ArrayList<>();

    public BuyListSeed(long currentMoney, int castleId) {
        _money = currentMoney;
        _manorId = castleId;

        for (SeedProduction s : CastleManorManager.getInstance().getSeedProduction(castleId, false)) {
            if ((s.getAmount() > 0) && (s.getPrice() > 0)) {
                _list.add(s);
            }
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.BUY_LIST_SEED);

        writeLong(_money); // current money
        writeInt(0x00); // TODO: Find me!
        writeInt(_manorId); // manor id

        if (!_list.isEmpty()) {
            writeShort((short) _list.size()); // list length
            for (SeedProduction s : _list) {
                writeByte((byte) 0x00); // mask item 0 to print minimal item information
                writeInt(s.getSeedId()); // ObjectId
                writeInt(s.getSeedId()); // ItemId
                writeByte((byte) 0xFF); // T1
                writeLong(s.getAmount()); // Quantity
                writeByte((byte) 0x05); // Item Type 2 : 00-weapon, 01-shield/armor, 02-ring/earring/necklace, 03-questitem, 04-adena, 05-item
                writeByte((byte) 0x00); // Filler (always 0)
                writeShort((short) 0x00); // Equipped : 00-No, 01-yes
                writeLong(0x00); // Slot : 0006-lr.ear, 0008-neck, 0030-lr.finger, 0040-head, 0100-l.hand, 0200-gloves, 0400-chest, 0800-pants, 1000-feet, 4000-r.hand, 8000-r.hand
                writeShort((short) 0x00); // Enchant level (pet level shown in control item)
                writeInt(-1);
                writeInt(-9999);
                writeByte((byte) 0x01); // GOD Item enabled = 1 disabled (red) = 0
                writeLong(s.getPrice()); // price
            }
            _list.clear();
        } else {
            writeShort((short) 0x00);
        }
    }

}
