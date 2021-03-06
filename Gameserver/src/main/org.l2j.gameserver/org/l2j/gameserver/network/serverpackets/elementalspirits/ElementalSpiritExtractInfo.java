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
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import static java.util.Objects.isNull;

public class ElementalSpiritExtractInfo extends ServerPacket {

    private final byte type;

    public ElementalSpiritExtractInfo(byte type) {
        this.type = type;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_ELEMENTAL_SPIRIT_EXTRACT_INFO, buffer );

        var spirit = client.getPlayer().getElementalSpirit(ElementalType.of(type));
        if(isNull(spirit)) {
            buffer.writeByte(0);
            buffer.writeByte(0);
            return;
        }

        buffer.writeByte(type); // active elemental spirit
        buffer.writeByte(1); // is extract ?
        
        buffer.writeByte(1); // cost count
         // for each cost count
        buffer.writeInt(57); // item id
        buffer.writeInt(1000000); // item count

        buffer.writeInt(spirit.getExtractItem());
        buffer.writeInt(spirit.getExtractAmount());
    }
}
