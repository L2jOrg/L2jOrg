package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class TradeDone extends ServerPacket {
    private final int _num;

    public TradeDone(int num) {
        _num = num;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.TRADE_DONE);

        writeInt(_num);
    }

}
