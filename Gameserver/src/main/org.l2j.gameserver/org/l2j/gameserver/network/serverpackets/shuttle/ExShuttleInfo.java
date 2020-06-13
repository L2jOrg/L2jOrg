/*
 * Copyright © 2019-2020 L2JOrg
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
package org.l2j.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Shuttle;
import org.l2j.gameserver.model.shuttle.ShuttleStop;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.List;

/**
 * @author UnAfraid
 */
public class ExShuttleInfo extends ServerPacket {
    private final Shuttle _shuttle;
    private final List<ShuttleStop> _stops;

    public ExShuttleInfo(Shuttle shuttle) {
        _shuttle = shuttle;
        _stops = shuttle.getStops();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHUTTLE_INFO);

        writeInt(_shuttle.getObjectId());
        writeInt(_shuttle.getX());
        writeInt(_shuttle.getY());
        writeInt(_shuttle.getZ());
        writeInt(_shuttle.getHeading());
        writeInt(_shuttle.getId());
        writeInt(_stops.size());
        for (ShuttleStop stop : _stops) {
            writeInt(stop.getId());
            for (Location loc : stop.getDimensions()) {
                writeInt(loc.getX());
                writeInt(loc.getY());
                writeInt(loc.getZ());
            }
            writeInt(stop.isDoorOpen() ? 0x01 : 0x00);
            writeInt(stop.hasDoorChanged() ? 0x01 : 0x00);
        }
    }

}
