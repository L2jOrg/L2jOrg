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
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class Ride extends ServerPacket {
    private final int _objectId;
    private final int _mounted;
    private final int _rideType;
    private final int _rideNpcId;
    private final Location _loc;

    public Ride(Player player) {
        _objectId = player.getObjectId();
        _mounted = player.isMounted() ? 1 : 0;
        _rideType = player.getMountType().ordinal();
        _rideNpcId = player.getMountNpcId() + 1000000;
        _loc = player.getLocation();
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.RIDE, buffer );

        buffer.writeInt(_objectId);
        buffer.writeInt(_mounted);
        buffer.writeInt(_rideType);
        buffer.writeInt(_rideNpcId);
        buffer.writeInt(_loc.getX());
        buffer.writeInt(_loc.getY());
        buffer.writeInt(_loc.getZ());
    }

}
