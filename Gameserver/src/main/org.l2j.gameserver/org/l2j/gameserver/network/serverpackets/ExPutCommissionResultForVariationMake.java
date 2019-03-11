/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ExPutCommissionResultForVariationMake extends IClientOutgoingPacket {
    private final int _gemstoneObjId;
    private final int _itemId;
    private final long _gemstoneCount;
    private final int _unk1;
    private final int _unk2;

    public ExPutCommissionResultForVariationMake(int gemstoneObjId, long count, int itemId) {
        _gemstoneObjId = gemstoneObjId;
        _itemId = itemId;
        _gemstoneCount = count;
        _unk1 = 0;
        _unk2 = 1;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PUT_COMMISSION_RESULT_FOR_VARIATION_MAKE.writeId(packet);

        packet.putInt(_gemstoneObjId);
        packet.putInt(_itemId);
        packet.putLong(_gemstoneCount);
        packet.putLong(_unk1);
        packet.putInt(_unk2);
    }
}
