package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ShowCalculator extends ServerPacket {
    private final int _calculatorId;

    public ShowCalculator(int calculatorId) {
        _calculatorId = calculatorId;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.SHOW_CALC);

        writeInt(_calculatorId);
    }

}