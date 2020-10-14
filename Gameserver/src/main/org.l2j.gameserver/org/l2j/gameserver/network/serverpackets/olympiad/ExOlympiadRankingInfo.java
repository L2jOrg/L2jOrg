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
public class ExOlympiadRankingInfo extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_OLYMPIAD_RANKING_INFO, buffer );
        buffer.writeByte(0); // type
        buffer.writeByte(0); // scope
        buffer.writeByte(true); // current season
        buffer.writeInt(12); // class Id
        buffer.writeInt(1); // world id

        buffer.writeInt(3); // total users

        buffer.writeInt(2); // rank size
        for (int i = 0; i < 2; i++) {
            buffer.writeSizedString("ranker" + i); // ranker name
            buffer.writeSizedString("rankerclan" + i); // ranker clan name
            buffer.writeInt(i+1); // rank
            buffer.writeInt(i); // prev rank
            buffer.writeInt(1); // ranker world id
            buffer.writeInt(76 +i); // ranker level
            buffer.writeInt(88 + i); // ranker class id
            buffer.writeInt(4); // ranker clan level
            buffer.writeInt( 4 + i); // ranker win count
            buffer.writeInt(5 + i); // ranker lose count
            buffer.writeInt(100 + i); // ranker points
            buffer.writeInt(2 + i); // hero count
            buffer.writeInt(5 + i); // legend count
        }
    }
}
