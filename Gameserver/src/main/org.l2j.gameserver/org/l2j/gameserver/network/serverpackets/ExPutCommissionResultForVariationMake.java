/*
 * Copyright © 2019 L2J Mobius
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

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

public class ExPutCommissionResultForVariationMake extends ServerPacket {
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
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_PUT_COMMISSION_RESULT_FOR_VARIATION_MAKE);

        writeInt(_gemstoneObjId);
        writeInt(_itemId);
        writeLong(_gemstoneCount);
        writeLong(_unk1);
        writeInt(_unk2);
    }

}
