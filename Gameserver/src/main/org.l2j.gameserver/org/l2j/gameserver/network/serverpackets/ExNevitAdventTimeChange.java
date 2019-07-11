package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author mochitto
 */
public class ExNevitAdventTimeChange extends ServerPacket {
    private final boolean _paused;
    private final int _time;

    public ExNevitAdventTimeChange(int time) {
        _time = time > 240000 ? 240000 : time;
        _paused = _time < 1;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_GET_CRYSTALIZING_ESTIMATION);

        // state 0 - pause 1 - started
        writeByte((byte) (_paused ? 0x00 : 0x01));
        // left time in ms max is 16000 its 4m and state is automatically changed to quit
        writeInt(_time);
    }

}
