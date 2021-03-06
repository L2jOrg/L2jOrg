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
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ValidateLocation extends ServerPacket {
    private final int _charObjId;
    private final Location _loc;

    public ValidateLocation(WorldObject obj) {
        _charObjId = obj.getObjectId();
        _loc = obj.getLocation();
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.VALIDATE_LOCATION, buffer );

        buffer.writeInt(_charObjId);
        buffer.writeInt(_loc.getX());
        buffer.writeInt(_loc.getY());
        buffer.writeInt(_loc.getZ());
        buffer.writeInt(_loc.getHeading());
        buffer.writeByte(0xFF); // TODO: Find me!
    }

}
