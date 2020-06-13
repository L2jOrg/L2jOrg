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

import org.l2j.gameserver.model.olympiad.OlympiadInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.List;

/**
 * @author JIV
 */
public class ExOlympiadMatchResult extends ServerPacket {
    private final boolean _tie;
    private final List<OlympiadInfo> _winnerList;
    private final List<OlympiadInfo> _loserList;
    private int _winTeam; // 1,2
    private int _loseTeam = 2;

    public ExOlympiadMatchResult(boolean tie, int winTeam, List<OlympiadInfo> winnerList, List<OlympiadInfo> loserList) {
        _tie = tie;
        _winTeam = winTeam;
        _winnerList = winnerList;
        _loserList = loserList;

        if (_winTeam == 2) {
            _loseTeam = 1;
        } else if (_winTeam == 0) {
            _winTeam = 1;
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_GFX_OLYMPIAD);

        writeInt(0x01); // Type 0 = Match List, 1 = Match Result

        writeInt(_tie ? 1 : 0); // 0 - win, 1 - tie
        writeString(_winnerList.get(0).getName());
        writeInt(_winTeam);
        writeInt(_winnerList.size());
        for (OlympiadInfo info : _winnerList) {
            writeParticipant(info);
        }

        writeInt(_loseTeam);
        writeInt(_loserList.size());
        for (OlympiadInfo info : _loserList) {
            writeParticipant(info);
        }
    }

    private void writeParticipant(OlympiadInfo info) {
        writeString(info.getName());
        writeString(info.getClanName());
        writeInt(info.getClanId());
        writeInt(info.getClassId());
        writeInt(info.getDamage());
        writeInt(info.getCurrentPoints());
        writeInt(info.getDiffPoints());
        writeInt(0x00); // Helios
    }

}
