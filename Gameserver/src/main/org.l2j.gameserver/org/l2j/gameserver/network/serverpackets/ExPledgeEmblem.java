package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.settings.ServerSettings;

import java.nio.ByteBuffer;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

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

        packet.putInt(getSettings(ServerSettings.class).serverId());
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

    @Override
    protected int size(L2GameClient client) {
        return 31 + (nonNull(_data) ? _data.length : 0);
    }
}