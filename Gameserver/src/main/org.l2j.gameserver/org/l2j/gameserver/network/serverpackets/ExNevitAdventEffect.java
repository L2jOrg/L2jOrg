package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author mochitto
 */
public class ExNevitAdventEffect extends IClientOutgoingPacket {
    private final int _timeLeft;

    public ExNevitAdventEffect(int timeLeft) {
        _timeLeft = timeLeft;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_CHANNELING_EFFECT.writeId(packet);

        packet.putInt(_timeLeft);
    }
}
