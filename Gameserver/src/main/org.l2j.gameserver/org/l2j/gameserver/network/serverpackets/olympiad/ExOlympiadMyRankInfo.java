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
import org.l2j.gameserver.engine.olympiad.Olympiad;
import org.l2j.gameserver.engine.olympiad.OlympiadBattleRecord;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Collection;
import java.util.Collections;

import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;

/**
 * @author JoeAlisson
 */
public class ExOlympiadMyRankInfo extends ServerPacket {

    private static final OlympiadRankData DEFAULT_OLYMPIAD_RANK_DATA = new OlympiadRankData();
    private final OlympiadRankData data;
    private final OlympiadRankData previousData;
    private final Collection<OlympiadBattleRecord> battleRecords;

    public ExOlympiadMyRankInfo(OlympiadRankData data, OlympiadRankData previousData, Collection<OlympiadBattleRecord> battleRecords) {
        this.data = requireNonNullElse(data, DEFAULT_OLYMPIAD_RANK_DATA);
        this.previousData = requireNonNullElse(previousData, DEFAULT_OLYMPIAD_RANK_DATA);
        this.battleRecords = requireNonNullElseGet(battleRecords, Collections::emptyList);
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_OLYMPIAD_MY_RANKING_INFO, buffer );
        final var olympiad = Olympiad.getInstance();
        buffer.writeInt(olympiad.getSeasonYear());
        buffer.writeInt(olympiad.getSeasonMonth());
        buffer.writeInt(olympiad.getCurrentSeason());
        buffer.writeInt(data.getRank());
        buffer.writeInt(data.getBattlesWon());
        buffer.writeInt(data.getBattlesLost());
        buffer.writeInt(data.getPoints());
        buffer.writeInt(previousData.getRank());
        buffer.writeInt(previousData.getBattlesWon());
        buffer.writeInt(previousData.getBattlesLost());
        buffer.writeInt(previousData.getPoints());

        buffer.writeInt(data.getHeroCount());
        buffer.writeInt(data.getLegendCount());

        buffer.writeInt(battleRecords.size());

        for (var record : battleRecords) {
            buffer.writeSizedString(record.name());
            buffer.writeByte(record.result());
            buffer.writeInt(record.level());
            buffer.writeInt(record.classId());
        }
    }
}
