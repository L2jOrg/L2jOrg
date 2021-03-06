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
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author UnAfraid
 */
public class ExTeleportToLocationActivate extends ServerPacket {
    private final int _objectId;
    private final Location _loc;

    public ExTeleportToLocationActivate(Creature character) {
        _objectId = character.getObjectId();
        _loc = character.getLocation();
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_TELEPORT_TO_LOCATION_ACTIVATE, buffer );

        buffer.writeInt(_objectId);
        buffer.writeInt(_loc.getX());
        buffer.writeInt(_loc.getY());
        buffer.writeInt(_loc.getZ());
        buffer.writeInt(0); // Unknown (this isn't instanceId)
        buffer.writeInt(_loc.getHeading());
        buffer.writeInt(0); // Unknown
    }

}