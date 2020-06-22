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
package org.l2j.gameserver.network.serverpackets.olympiad;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExOlympiadRecord extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerExPacketId.EX_OLYMPIAD_RECORD);

        // current season
        writeInt(0); // points
        writeInt(0); // win count
        writeInt(0); // lose count
        writeInt(5); // match left (MAX 5)

        // From olympiad history
        writeInt(0); // prev class type

        writeInt(0);  // prev rank (non classed all servers)
        writeInt(0); // prev rank count

        writeInt(0); // prev class rank
        writeInt(0); // prev class rank count

        writeInt(0); // prev class rank by server
        writeInt(0); // prev class rank by server count

        writeInt(0); // prev point
        writeInt(0); // prev win count
        writeInt(0); // prev lose count

        writeInt(0); // prev grade

        writeInt(2020); // season year
        writeInt(6); // season month
        writeByte(true); // match open
        writeInt(2); // season
        writeByte(false); // registered
        writeByte(1); // game rule type (0 - 3v3)
    }
}
