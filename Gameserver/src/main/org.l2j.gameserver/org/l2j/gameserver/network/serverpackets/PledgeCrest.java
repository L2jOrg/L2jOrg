package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.database.data.CrestData;
import org.l2j.gameserver.data.sql.impl.CrestTable;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.settings.ServerSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

public final class PledgeCrest extends ServerPacket {
    private final int _crestId;
    private final byte[] _data;

    public PledgeCrest(int crestId) {
        _crestId = crestId;
        final CrestData crest = CrestTable.getInstance().getCrest(crestId);
        _data = crest != null ? crest.getData() : null;
    }

    public PledgeCrest(int crestId, byte[] data) {
        _crestId = crestId;
        _data = data;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PLEDGE_CREST);

        writeInt(getSettings(ServerSettings.class).serverId());
        writeInt(_crestId);
        if (_data != null) {
            writeInt(_data.length);
            writeBytes(_data);
        } else {
            writeInt(0);
        }
    }

}
