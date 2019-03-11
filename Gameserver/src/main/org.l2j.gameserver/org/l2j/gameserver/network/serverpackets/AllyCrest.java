package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.CrestTable;
import org.l2j.gameserver.model.L2Crest;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class AllyCrest extends IClientOutgoingPacket {
    private final int _crestId;
    private final byte[] _data;

    public AllyCrest(int crestId) {
        _crestId = crestId;
        final L2Crest crest = CrestTable.getInstance().getCrest(crestId);
        _data = crest != null ? crest.getData() : null;
    }

    public AllyCrest(int crestId, byte[] data) {
        _crestId = crestId;
        _data = data;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.ALLIANCE_CREST.writeId(packet);

        packet.putInt(Config.SERVER_ID);
        packet.putInt(_crestId);
        if (_data != null) {
            packet.putInt(_data.length);
            packet.put(_data);
        } else {
            packet.putInt(0);
        }
    }
}
