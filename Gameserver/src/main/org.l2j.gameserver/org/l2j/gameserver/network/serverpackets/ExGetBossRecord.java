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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Map;
import java.util.Map.Entry;

/**
 * @author KenM
 */
public class ExGetBossRecord extends ServerPacket {
    private final Map<Integer, Integer> _bossRecordInfo;
    private final int _ranking;
    private final int _totalPoints;

    public ExGetBossRecord(int ranking, int totalScore, Map<Integer, Integer> list) {
        _ranking = ranking;
        _totalPoints = totalScore;
        _bossRecordInfo = list;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_GET_BOSS_RECORD, buffer );

        buffer.writeInt(_ranking);
        buffer.writeInt(_totalPoints);
        if (_bossRecordInfo == null) {
            buffer.writeInt(0x00);
            buffer.writeInt(0x00);
            buffer.writeInt(0x00);
            buffer.writeInt(0x00);
        } else {
            buffer.writeInt(_bossRecordInfo.size()); // list size
            for (Entry<Integer, Integer> entry : _bossRecordInfo.entrySet()) {
                buffer.writeInt(entry.getKey());
                buffer.writeInt(entry.getValue());
                buffer.writeInt(0x00); // ??
            }
        }
    }

}
