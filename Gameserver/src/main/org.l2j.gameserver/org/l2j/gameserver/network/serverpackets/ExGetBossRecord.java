package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Objects.nonNull;

/**
 * @author KenM
 */
public class ExGetBossRecord extends IClientOutgoingPacket {
    private final Map<Integer, Integer> _bossRecordInfo;
    private final int _ranking;
    private final int _totalPoints;

    public ExGetBossRecord(int ranking, int totalScore, Map<Integer, Integer> list) {
        _ranking = ranking;
        _totalPoints = totalScore;
        _bossRecordInfo = list;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_GET_BOSS_RECORD);

        writeInt(_ranking);
        writeInt(_totalPoints);
        if (_bossRecordInfo == null) {
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
        } else {
            writeInt(_bossRecordInfo.size()); // list size
            for (Entry<Integer, Integer> entry : _bossRecordInfo.entrySet()) {
                writeInt(entry.getKey());
                writeInt(entry.getValue());
                writeInt(0x00); // ??
            }
        }
    }

}
