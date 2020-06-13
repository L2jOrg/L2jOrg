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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * Halloween rank list server packet.
 */
public class ExBrLoadEventTopRankers extends ServerPacket {
    private final int _eventId;
    private final int _day;
    private final int _count;
    private final int _bestScore;
    private final int _myScore;

    public ExBrLoadEventTopRankers(int eventId, int day, int count, int bestScore, int myScore) {
        _eventId = eventId;
        _day = day;
        _count = count;
        _bestScore = bestScore;
        _myScore = myScore;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_BR_LOAD_EVENT_TOP_RANKERS_ACK);

        writeInt(_eventId);
        writeInt(_day);
        writeInt(_count);
        writeInt(_bestScore);
        writeInt(_myScore);
    }

}
