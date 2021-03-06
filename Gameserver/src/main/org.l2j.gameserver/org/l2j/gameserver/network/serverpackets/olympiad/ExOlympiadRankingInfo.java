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
package org.l2j.gameserver.network.serverpackets.olympiad;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.data.database.data.OlympiadRankData;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.List;

/**
 * @author JoeAlisson
 */
public class ExOlympiadRankingInfo extends ServerPacket {

    private final List<OlympiadRankData> rankers;
    private final byte type;
    private final byte scope;
    private final boolean currentSeason;
    private final int classId;
    private final int server;
    private final int participants;

    public ExOlympiadRankingInfo(byte type, byte scope, boolean currentSeason, int classId, int server, List<OlympiadRankData> rankers, int participants) {
        this.type = type;
        this.scope = scope;
        this.currentSeason = currentSeason;
        this.classId = classId;
        this.server = server;
        this.rankers = rankers;
        this.participants = participants;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_OLYMPIAD_RANKING_INFO, buffer );
        buffer.writeByte(type);
        buffer.writeByte(scope);
        buffer.writeByte(currentSeason);
        buffer.writeInt(classId);
        buffer.writeInt(server);

        buffer.writeInt(participants);
        buffer.writeInt(rankers.size());
        for (var ranker : rankers) {
            buffer.writeSizedString(ranker.getName());
            buffer.writeSizedString(ranker.getClanName());
            buffer.writeInt(ranker.getRank());
            buffer.writeInt(ranker.getPreviousRank());
            buffer.writeInt(ranker.getServer());
            buffer.writeInt(ranker.getLevel());
            buffer.writeInt(ranker.getClassId());
            buffer.writeInt(ranker.getClanLevel());
            buffer.writeInt(ranker.getBattlesWon());
            buffer.writeInt(ranker.getBattlesLost());
            buffer.writeInt(ranker.getPoints());
            buffer.writeInt(ranker.getHeroCount());
            buffer.writeInt(ranker.getLegendCount());
        }
    }
}
