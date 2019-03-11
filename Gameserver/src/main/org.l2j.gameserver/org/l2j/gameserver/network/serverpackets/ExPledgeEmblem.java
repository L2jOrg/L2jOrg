package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author -Wooden-, Sdw
 */
public class ExPledgeEmblem extends IClientOutgoingPacket {
    private static final int TOTAL_SIZE = 65664;
    private final int _crestId;
    private final int _clanId;
    private final byte[] _data;
    private final int _chunkId;

    public ExPledgeEmblem(int crestId, byte[] chunkedData, int clanId, int chunkId) {
        _crestId = crestId;
        _data = chunkedData;
        _clanId = clanId;
        _chunkId = chunkId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PLEDGE_EMBLEM.writeId(packet);

        packet.putInt(Config.SERVER_ID);
        packet.putInt(_clanId);
        packet.putInt(_crestId);
        packet.putInt(_chunkId);
        packet.putInt(TOTAL_SIZE);
        if (_data != null) {
            packet.putInt(_data.length);
            packet.put(_data);
        } else {
            packet.putInt(0);
        }
    }
}