package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author janiii
 */
public class ExEventMatchMessage extends IClientOutgoingPacket {
    private final int _type;
    private final String _message;

    /**
     * Create an event match message.
     *
     * @param type    0 - gm, 1 - finish, 2 - start, 3 - game over, 4 - 1, 5 - 2, 6 - 3, 7 - 4, 8 - 5
     * @param message message to show, only when type is 0 - gm
     */
    public ExEventMatchMessage(int type, String message) {
        _type = type;
        _message = message;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_EVENT_MATCH_MESSAGE.writeId(packet);

        packet.put((byte) _type);
        writeString(_message, packet);
    }
}
