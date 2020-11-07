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

    public ExOlympiadRecord(OlympiadParticipantData data) {
        participantData = data;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer)  {
        writeId(ServerExPacketId.EX_OLYMPIAD_RECORD, buffer );

        var olympiad = Olympiad.getInstance();
        // current season
        buffer.writeInt(participantData.getPoints());
        buffer.writeInt(participantData.getBattlesWon());
        buffer.writeInt(participantData.getBattlesLost());
        buffer.writeInt(olympiad.getMaxBattlesPerDay() - participantData.getBattlesToday()); // match left (MAX 5)

        // From olympiad history
        buffer.writeInt(127); // prev class type

        buffer.writeInt(10);  // prev rank (non classed all servers)
        buffer.writeInt(20); // prev rank count

        buffer.writeInt(5); // prev class rank
        buffer.writeInt(10); // prev class rank count

        buffer.writeInt(2); // prev class rank by server
        buffer.writeInt(10); // prev class rank by server count

        buffer.writeInt(230); // prev point
        buffer.writeInt(30); // prev win count
        buffer.writeInt(20); // prev lose count

        buffer.writeInt(100); // prev grade

        buffer.writeInt(olympiad.getSeasonYear());
        buffer.writeInt(olympiad.getSeasonMonth());
        buffer.writeByte(olympiad.isMatchesInProgress());
        buffer.writeInt(olympiad.getCurrentSeason());
        buffer.writeByte(olympiad.isRegistered(client.getPlayer()));
        buffer.writeByte(OlympiadRuleType.CLASSLESS.ordinal());
    }
}
