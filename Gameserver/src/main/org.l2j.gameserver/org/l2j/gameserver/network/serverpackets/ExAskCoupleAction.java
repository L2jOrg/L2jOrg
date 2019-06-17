package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JIV
 */
public class ExAskCoupleAction extends ServerPacket {
    private final int _charObjId;
    private final int _actionId;

    public ExAskCoupleAction(int charObjId, int social) {
        _charObjId = charObjId;
        _actionId = social;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_ASK_COUPLE_ACTION);

        writeInt(_actionId);
        writeInt(_charObjId);
    }

}
