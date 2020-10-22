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
import org.l2j.gameserver.model.olympiad.OlympiadInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.List;

/**
 * @author JIV
 */
public class ExOlympiadMatchResult extends ServerPacket {
    private static final int LOSER_MASK = 3;
    private final boolean _tie;
    private final List<OlympiadInfo> _winnerList;
    private final List<OlympiadInfo> _loserList;
    private final int _winTeam; // 1,2

    public ExOlympiadMatchResult(boolean tie, int winTeam, List<OlympiadInfo> winnerList, List<OlympiadInfo> loserList) {
        _tie = tie;
        _winTeam = winTeam;
        _winnerList = winnerList;
        _loserList = loserList;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_GFX_OLYMPIAD, buffer );

        buffer.writeInt(0x01); // Type 0 = Match List, 1 = Match Result
        buffer.writeInt(_tie); // 0 - win, 1 - tie
        buffer.writeString(_winnerList.get(0).getName());
        buffer.writeInt(_winTeam);
        buffer.writeInt(_winnerList.size());
        for (OlympiadInfo info : _winnerList) {
            writeParticipant(info, buffer);
        }

        buffer.writeInt(_winTeam ^ LOSER_MASK);
        buffer.writeInt(_loserList.size());
        for (OlympiadInfo info : _loserList) {
            writeParticipant(info, buffer);
        }
    }

    private void writeParticipant(OlympiadInfo info, WritableBuffer buffer) {
        buffer.writeString(info.getName());
        buffer.writeString(info.getClanName());
        buffer.writeInt(info.getClanId());
        buffer.writeInt(info.getClassId());
        buffer.writeInt(info.getDamage());
        buffer.writeInt(info.getCurrentPoints());
        buffer.writeInt(info.getDiffPoints());
        buffer.writeInt(0x00); // Helios
    }

}
