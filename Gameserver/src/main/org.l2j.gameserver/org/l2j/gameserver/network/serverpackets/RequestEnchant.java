package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author nBd
 */
public class RequestEnchant extends ServerPacket {
    private final int _result;

    public RequestEnchant(int result) {
        _result = result;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_PRIVATE_STORE_WHOLE_MSG);

        writeInt(_result);
    }

}
