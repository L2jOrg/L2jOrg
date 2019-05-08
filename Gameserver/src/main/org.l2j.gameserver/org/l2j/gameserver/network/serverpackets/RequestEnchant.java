package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author nBd
 */
public class RequestEnchant extends IClientOutgoingPacket {
    private final int _result;

    public RequestEnchant(int result) {
        _result = result;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PRIVATE_STORE_WHOLE_MSG.writeId(packet);

        packet.putInt(_result);
    }

    @Override
    protected int size(L2GameClient client) {
        return 9;
    }
}
