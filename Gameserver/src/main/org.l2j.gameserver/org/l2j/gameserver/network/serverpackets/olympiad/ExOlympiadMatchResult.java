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
import org.l2j.gameserver.engine.olympiad.OlympiadMode;
import org.l2j.gameserver.engine.olympiad.OlympiadResultInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.List;

/**
 * @author JIV
 * @author JoeAlisson
 */
public class ExOlympiadMatchResult extends ServerPacket {
    private static final int LOSER_MASK = 3;
    private final boolean tie;
    private final List<OlympiadResultInfo> winnerList;
    private final List<OlympiadResultInfo> loserList;
    private final int winTeam; // 1,2

    public ExOlympiadMatchResult(boolean tie, int winTeam, List<OlympiadResultInfo> winnerList, List<OlympiadResultInfo> loserList) {
        this.tie = tie;
        this.winTeam = winTeam;
        this.winnerList = winnerList;
        this.loserList = loserList;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_GFX_OLYMPIAD, buffer );

        buffer.writeInt(0x01); // Type 0 = Match List, 1 = Match Result

        buffer.writeInt(tie);
        buffer.writeString(winnerList.get(0).getName());

        buffer.writeInt(winTeam);
        buffer.writeInt(winnerList.size());
        for (OlympiadResultInfo info : winnerList) {
            writeParticipant(info, buffer);
        }

        buffer.writeInt(winTeam ^ LOSER_MASK);
        buffer.writeInt(loserList.size());
        for (OlympiadResultInfo info : loserList) {
            writeParticipant(info, buffer);
        }
    }

    private void writeParticipant(OlympiadResultInfo info, WritableBuffer buffer) {
        buffer.writeString(info.getName());
        buffer.writeString(info.getClanName());
        buffer.writeInt(info.getClanId());
        buffer.writeInt(info.getClassId());
        buffer.writeInt(info.getDamage());
        buffer.writeInt(info.getCurrentPoints());
        buffer.writeInt(info.getDiffPoints());
        buffer.writeInt(0x01); // Helios
    }

    public static ExOlympiadMatchResult tie(List<OlympiadResultInfo> redTeam, List<OlympiadResultInfo> blueTeam) {
        return new ExOlympiadMatchResult(true, OlympiadMode.RED.ordinal(), redTeam, blueTeam);
    }

    public static ExOlympiadMatchResult victory(OlympiadMode winnerMode, List<OlympiadResultInfo> winnerTeam, List<OlympiadResultInfo> loserTeam) {
        return new ExOlympiadMatchResult(false, winnerMode.ordinal(), winnerTeam, loserTeam);
    }
}
