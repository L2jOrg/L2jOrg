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
public class ExOlympiadRankingInfo extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_OLYMPIAD_RANKING_INFO);
        writeByte(0); // type
        writeByte(0); // scope
        writeByte(true); // current season
        writeInt(12); // class Id
        writeInt(1); // world id

        writeInt(3); // total users

        writeInt(2); // rank size
        for (int i = 0; i < 2; i++) {
            writeSizedString("ranker" + i); // ranker name
            writeSizedString("rankerclan" + i); // ranker clan name
            writeInt(i+1); // rank
            writeInt(i); // prev rank
            writeInt(1); // ranker world id
            writeInt(76 +i); // ranker level
            writeInt(88 + i); // ranker class id
            writeInt(4); // ranker clan level
            writeInt( 4 + i); // ranker win count
            writeInt(5 + i); // ranker lose count
            writeInt(100 + i); // ranker points
            writeInt(2 + i); // hero count
            writeInt(5 + i); // legend count
        }
    }
}
