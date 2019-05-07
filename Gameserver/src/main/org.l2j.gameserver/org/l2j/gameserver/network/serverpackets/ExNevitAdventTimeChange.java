package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author mochitto
 */
public class ExNevitAdventTimeChange extends IClientOutgoingPacket {
    private final boolean _paused;
    private final int _time;

    public ExNevitAdventTimeChange(int time) {
        _time = time > 240000 ? 240000 : time;
        _paused = _time < 1;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_GET_CRYSTALIZING_ESTIMATION.writeId(packet);

        // state 0 - pause 1 - started
        packet.put((byte) (_paused ? 0x00 : 0x01));
        // left time in ms max is 16000 its 4m and state is automatically changed to quit
        packet.putInt(_time);
    }

    @Override
    protected int size(L2GameClient client) {
        return 10;
    }
}
