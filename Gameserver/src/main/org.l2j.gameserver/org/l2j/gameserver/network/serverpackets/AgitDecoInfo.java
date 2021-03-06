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
import org.l2j.gameserver.model.residences.AbstractResidence;
import org.l2j.gameserver.model.residences.ResidenceFunctionType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Steuf, UnAfraid
 */
public class AgitDecoInfo extends ServerPacket {
    private final AbstractResidence _residense;

    public AgitDecoInfo(AbstractResidence residense) {
        _residense = residense;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.AGIT_DECO_INFO, buffer );
        buffer.writeInt(_residense.getId());
        for (ResidenceFunctionType type : ResidenceFunctionType.values()) {
            if (type == ResidenceFunctionType.NONE) {
                continue;
            }
            buffer.writeByte(_residense.hasFunction(type));
        }

        // Unknown
        buffer.writeInt(0); // TODO: Find me!
        buffer.writeInt(0); // TODO: Find me!
        buffer.writeInt(0); // TODO: Find me!
        buffer.writeInt(0); // TODO: Find me!
        buffer.writeInt(0); // TODO: Find me!
    }

}
