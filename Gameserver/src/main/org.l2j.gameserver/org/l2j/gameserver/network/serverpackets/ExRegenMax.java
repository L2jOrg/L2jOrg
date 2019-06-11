package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ExRegenMax extends IClientOutgoingPacket {
    private final int _time;
    private final int _tickInterval;
    private final double _amountPerTick;

    public ExRegenMax(int time, int tickInterval, double amountPerTick) {
        _time = time;
        _tickInterval = tickInterval;
        _amountPerTick = amountPerTick;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_REGEN_MAX);

        writeInt(1);
        writeInt(_time);
        writeInt(_tickInterval);
        writeDouble(_amountPerTick);
    }

}
