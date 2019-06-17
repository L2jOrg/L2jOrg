package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JIV
 */
public class ExRotation extends ServerPacket {
    private final int _charId;
    private final int _heading;

    public ExRotation(int charId, int heading) {
        _charId = charId;
        _heading = heading;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_ROTATION);

        writeInt(_charId);
        writeInt(_heading);
    }

}
