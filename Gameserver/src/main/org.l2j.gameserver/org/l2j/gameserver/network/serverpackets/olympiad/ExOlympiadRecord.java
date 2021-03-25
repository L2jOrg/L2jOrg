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
import org.l2j.gameserver.data.database.data.OlympiadHistoryData;
import org.l2j.gameserver.data.database.data.OlympiadParticipantData;
import org.l2j.gameserver.engine.olympiad.Olympiad;
import org.l2j.gameserver.engine.olympiad.OlympiadRuleType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExOlympiadRecord extends ServerPacket {

    private final OlympiadParticipantData participantData;
    private final OlympiadHistoryData lastCycleData;

    public ExOlympiadRecord(OlympiadParticipantData data, OlympiadHistoryData lastCycleData) {
        participantData = data;
        this.lastCycleData = lastCycleData;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer)  {
        writeId(ServerExPacketId.EX_OLYMPIAD_RECORD, buffer );

        var olympiad = Olympiad.getInstance();

        buffer.writeInt(participantData.getPoints());
        buffer.writeInt(participantData.getBattlesWon());
        buffer.writeInt(participantData.getBattlesLost());
        buffer.writeInt(olympiad.getMaxBattlesPerDay() - participantData.getBattlesToday()); // match left (MAX 5)


        buffer.writeInt(lastCycleData.getClassId());

        buffer.writeInt(lastCycleData.getOverallRank());
        buffer.writeInt(lastCycleData.getOverallCount());

        buffer.writeInt(lastCycleData.getOverallClassRank());
        buffer.writeInt(lastCycleData.getOverallClassCount());

        buffer.writeInt(lastCycleData.getServerClassRank());
        buffer.writeInt(lastCycleData.getServerClassCount());

        buffer.writeInt(lastCycleData.getPoints());
        buffer.writeInt(lastCycleData.getBattlesOwn());
        buffer.writeInt(lastCycleData.getBattlesLost());

        buffer.writeInt(0); // prev grade

        buffer.writeInt(olympiad.getSeasonYear());
        buffer.writeInt(olympiad.getSeasonMonth());
        buffer.writeByte(olympiad.isMatchesInProgress());
        buffer.writeInt(olympiad.getCurrentSeason());
        buffer.writeByte(olympiad.isRegistered(client.getPlayer()));
        buffer.writeByte(OlympiadRuleType.CLASSLESS.ordinal());
    }
}
