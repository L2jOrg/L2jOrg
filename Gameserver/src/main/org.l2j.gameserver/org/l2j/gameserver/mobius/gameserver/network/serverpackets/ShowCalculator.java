package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ShowCalculator extends IClientOutgoingPacket {
    private final int _calculatorId;

    public ShowCalculator(int calculatorId) {
        _calculatorId = calculatorId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SHOW_CALC.writeId(packet);

        packet.putInt(_calculatorId);
    }
}