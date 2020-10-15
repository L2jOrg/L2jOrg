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

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExOlympiadMyRankInfo extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_OLYMPIAD_MY_RANKING_INFO, buffer );
        buffer.writeInt(2020); // season year
        buffer.writeInt(3); // season month
        buffer.writeInt(1); // season
        buffer.writeInt(2); // rank
        buffer.writeInt(5); // win count
        buffer.writeInt(2); // lose count
        buffer.writeInt(100); // points

        buffer.writeInt(3); // prev rank
        buffer.writeInt(8); // prev win count
        buffer.writeInt(1); // prev lose count
        buffer.writeInt(150); // prev points

        buffer.writeInt(5); // hero count
        buffer.writeInt(2); // legend count

        buffer.writeInt(3); // recent matches count

        for (int i = 0; i < 3; i++) {
            buffer.writeSizedString("Enemy" + i); // enemy name
            buffer.writeByte(i %2 == 0); // lost ?
            buffer.writeInt(75 + i); // enemy level
            buffer.writeInt(88 + i); // enemy class
        }
    }
}
