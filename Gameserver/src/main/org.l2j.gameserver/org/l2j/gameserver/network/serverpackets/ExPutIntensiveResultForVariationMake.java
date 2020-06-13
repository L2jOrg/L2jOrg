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

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

public class ExPutIntensiveResultForVariationMake extends ServerPacket {
    private final int _refinerItemObjId;
    private final int _lifestoneItemId;
    private final int _gemstoneItemId;
    private final long _gemstoneCount;
    private final int _unk2;

    public ExPutIntensiveResultForVariationMake(int refinerItemObjId, int lifeStoneId, int gemstoneItemId, long gemstoneCount) {
        _refinerItemObjId = refinerItemObjId;
        _lifestoneItemId = lifeStoneId;
        _gemstoneItemId = gemstoneItemId;
        _gemstoneCount = gemstoneCount;
        _unk2 = 1;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_PUT_INTENSIVE_RESULT_FOR_VARIATION_MAKE);

        writeInt(_refinerItemObjId);
        writeInt(_lifestoneItemId);
        writeInt(_gemstoneItemId);
        writeLong(_gemstoneCount);
        writeInt(_unk2);
    }

}
