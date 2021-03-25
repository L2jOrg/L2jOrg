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

/**
 * @author Maktakien
 */
public class MoveToLocationInVehicle extends ServerPacket {
    private final int _charObjId;
    private final int _boatId;
    private final Location _destination;
    private final Location _origin;

    /**
     * @param player
     * @param destination
     * @param origin
     */
    public MoveToLocationInVehicle(Player player, Location destination, Location origin) {
        _charObjId = player.getObjectId();
        _boatId = player.getBoat().getObjectId();
        _destination = destination;
        _origin = origin;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.MOVE_TO_LOCATION_IN_VEHICLE, buffer );

        buffer.writeInt(_charObjId);
        buffer.writeInt(_boatId);
        buffer.writeInt(_destination.getX());
        buffer.writeInt(_destination.getY());
        buffer.writeInt(_destination.getZ());
        buffer.writeInt(_origin.getX());
        buffer.writeInt(_origin.getY());
        buffer.writeInt(_origin.getZ());
    }

}
