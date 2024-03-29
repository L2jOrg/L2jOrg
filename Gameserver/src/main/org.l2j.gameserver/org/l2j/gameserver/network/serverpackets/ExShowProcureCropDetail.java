/*
 * Copyright © 2019-2021 L2JOrg
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

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Collections;
import java.util.Map;

/**
 * @author l3x
 */
public class ExShowProcureCropDetail extends ServerPacket {
    private final int cropId;
    private final Map<Integer, Object> castleCrops = Collections.emptyMap();

    public ExShowProcureCropDetail(int cropId) {
        this.cropId = cropId;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_SHOW_PROCURE_CROP_DETAIL, buffer );

        buffer.writeInt(cropId); // crop id
        buffer.writeInt(castleCrops.size()); // size

        for (Map.Entry<Integer, Object> entry : castleCrops.entrySet()) {
            buffer.writeInt(entry.getKey()); // manor name
            buffer.writeLong(0x00); // buy residual amount
            buffer.writeLong(0x00); // buy price
            buffer.writeByte(0x00); // reward type
        }
    }

}
