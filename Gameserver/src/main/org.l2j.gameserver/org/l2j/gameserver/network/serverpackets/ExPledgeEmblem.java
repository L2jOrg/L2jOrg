package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.settings.ServerSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author -Wooden-, Sdw
 */
public class ExPledgeEmblem extends ServerPacket {
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
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_PLEDGE_EMBLEM);

        writeInt(getSettings(ServerSettings.class).serverId());
        writeInt(_clanId);
        writeInt(_crestId);
        writeInt(_chunkId);
        writeInt(TOTAL_SIZE);
        if (_data != null) {
            writeInt(_data.length);
            writeBytes(_data);
        } else {
            writeInt(0);
        }
    }

}