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
package org.l2j.gameserver.network.serverpackets.elementalspirits;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import static java.util.Objects.isNull;

public class ElementalSpiritInfo extends AbstractElementalSpiritPacket {

    private final byte spiritType;
    private final byte type;

    public ElementalSpiritInfo(byte spiritType, byte packetType) {
        this.spiritType = spiritType;
        this.type = packetType;
    }

    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_ELEMENTAL_SPIRIT_INFO, buffer );

        var player = client.getPlayer();
        var spirits = player.getSpirits();

        if(isNull(spirits)) {
            buffer.writeByte(0);
            buffer.writeByte(0);
            buffer.writeByte(0);
            return;
        }

        buffer.writeByte(type); // show spirit info window 1; Change type 2; Only update 0
        buffer.writeByte(spiritType);

        buffer.writeByte(spirits.length); // spirit count

        for (ElementalSpirit spirit : spirits) {
            buffer.writeByte(spirit.getType());
            buffer.writeByte(0x01); // spirit active ?
            // if active
            writeSpiritInfo(spirit, buffer);
        }

        buffer.writeInt(1); // Reset talent item count
        for (int j = 0; j < 1; j++) {
            buffer.writeInt(57);
            buffer.writeLong(50000);
        }
    }
}