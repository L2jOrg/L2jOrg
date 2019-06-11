package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author JIV
 */
public class ExAskCoupleAction extends IClientOutgoingPacket {
    private final int _charObjId;
    private final int _actionId;

    public ExAskCoupleAction(int charObjId, int social) {
        _charObjId = charObjId;
        _actionId = social;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_ASK_COUPLE_ACTION);

        writeInt(_actionId);
        writeInt(_charObjId);
    }

}
