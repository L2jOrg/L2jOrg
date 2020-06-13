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
public class ExOlympiadMyRankInfo extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_OLYMPIAD_MY_RANKING_INFO);
        writeInt(2020); // season year
        writeInt(3); // season month
        writeInt(1); // season
        writeInt(2); // rank
        writeInt(5); // win count
        writeInt(2); // lose count
        writeInt(100); // points

        writeInt(3); // prev rank
        writeInt(8); // prev win count
        writeInt(1); // prev lose count
        writeInt(150); // prev points

        writeInt(5); // hero count
        writeInt(2); // legend count

        writeInt(3); // recent matches count

        for (int i = 0; i < 3; i++) {
            writeSizedString("Enemy" + i); // enemy name
            writeByte(i %2 == 0); // lost ?
            writeInt(75 + i); // enemy level
            writeInt(88 + i); // enemy class
        }
    }
}
