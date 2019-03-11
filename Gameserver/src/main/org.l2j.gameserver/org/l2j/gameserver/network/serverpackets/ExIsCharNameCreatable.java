package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class ExIsCharNameCreatable extends IClientOutgoingPacket {
    private final int _allowed;

    public ExIsCharNameCreatable(int allowed) {
        _allowed = allowed;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_IS_CHAR_NAME_CREATABLE.writeId(packet);

        packet.putInt(_allowed);
    }
}
