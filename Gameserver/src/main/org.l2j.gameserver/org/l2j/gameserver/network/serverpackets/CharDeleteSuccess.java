package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class CharDeleteSuccess extends IClientOutgoingPacket {
    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.CHARACTER_DELETE_SUCCESS.writeId(packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return 5;
    }
}
