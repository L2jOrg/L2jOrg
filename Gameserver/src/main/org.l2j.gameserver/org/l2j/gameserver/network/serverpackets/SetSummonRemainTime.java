package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class SetSummonRemainTime extends ServerPacket {
    private final int _maxTime;
    private final int _remainingTime;

    public SetSummonRemainTime(int maxTime, int remainingTime) {
        _remainingTime = remainingTime;
        _maxTime = maxTime;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SET_SUMMON_REMAIN_TIME);

        writeInt(_maxTime);
        writeInt(_remainingTime);
    }

}
