package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

public class ExRegenMax extends ServerPacket {
    private final int _time;
    private final int _tickInterval;
    private final double _amountPerTick;

    public ExRegenMax(int time, int tickInterval, double amountPerTick) {
        _time = time;
        _tickInterval = tickInterval;
        _amountPerTick = amountPerTick;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_REGEN_MAX);

        writeInt(1);
        writeInt(_time);
        writeInt(_tickInterval);
        writeDouble(_amountPerTick);
    }

}
