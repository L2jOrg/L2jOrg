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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_GET_BOSS_RECORD.writeId(packet);

        packet.putInt(_ranking);
        packet.putInt(_totalPoints);
        if (_bossRecordInfo == null) {
            packet.putInt(0x00);
            packet.putInt(0x00);
            packet.putInt(0x00);
            packet.putInt(0x00);
        } else {
            packet.putInt(_bossRecordInfo.size()); // list size
            for (Entry<Integer, Integer> entry : _bossRecordInfo.entrySet()) {
                packet.putInt(entry.getKey());
                packet.putInt(entry.getValue());
                packet.putInt(0x00); // ??
            }
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 17 + (nonNull(_bossRecordInfo) ?  _bossRecordInfo.size() * 12 : 16);
    }
}
