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
public final class ExShowManorDefaultInfo extends ServerPacket {
    private final List<Object> crops = Collections.emptyList();
    private final boolean _hideButtons;

    public ExShowManorDefaultInfo(boolean hideButtons) {
        _hideButtons = hideButtons;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_SHOW_MANOR_DEFAULT_INFO, buffer );

        buffer.writeByte(_hideButtons); // Hide "Seed Purchase" and "Crop Sales" buttons
        buffer.writeInt(crops.size());
        // for each crop
            buffer.writeInt(0x00); // crop Id
            buffer.writeInt(0x00); // level
            buffer.writeInt(0x00); // seed price
            buffer.writeInt(0x00); // crop price
            buffer.writeByte(1); // Reward 1 type
            buffer.writeInt(0x00); // Reward 1 itemId
            buffer.writeByte(1); // Reward 2 type
            buffer.writeInt(0x00); // Reward 2 itemId

    }

}