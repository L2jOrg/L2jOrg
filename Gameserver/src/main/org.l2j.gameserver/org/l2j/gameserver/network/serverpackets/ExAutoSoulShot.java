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
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author JoeAlisson
 */
public class ExAutoSoulShot extends ServerPacket {
    private final int itemId;
    private final boolean enable;
    private final ShotType type;

    public ExAutoSoulShot(int itemId, boolean enable, ShotType type) {
        this.itemId = itemId;
        this.enable = enable;
        this.type = type;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_AUTO_SOULSHOT, buffer );

        buffer.writeInt(itemId);
        buffer.writeInt(enable);
        buffer.writeInt(type.getClientId());
    }

}
