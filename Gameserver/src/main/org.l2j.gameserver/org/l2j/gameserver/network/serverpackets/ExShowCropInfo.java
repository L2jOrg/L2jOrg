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
import java.util.List;

/**
 * @author l3x
 */
public class ExShowCropInfo extends ServerPacket {
    private final List<Object> crops = Collections.emptyList();
    private final int _manorId;
    private final boolean _hideButtons;

    public ExShowCropInfo(int manorId, boolean hideButtons) {
        _manorId = manorId;
        _hideButtons = hideButtons;

    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_SHOW_CROP_INFO, buffer );

        buffer.writeByte(_hideButtons); // Hide "Crop Sales" button
        buffer.writeInt(_manorId); // Manor ID
        buffer.writeInt(0x00);

        buffer.writeInt(crops.size());
        // for each crop
            buffer.writeInt(0x00); // Crop id
            buffer.writeLong(0x00); // Buy amount residual
            buffer.writeLong(0x00); // Buy start amount
            buffer.writeLong(0x00); // Buy price
            buffer.writeByte(0x00); // Reward

            buffer.writeInt(0); // Seed level
            buffer.writeByte(0x01); // Reward 1
            buffer.writeInt(0); // Reward 1 - item id
            buffer.writeByte(0x01); // Reward 2
            buffer.writeInt(0); // Reward 2 - item id
    }

}