package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JIV
 */
public class ExBaseAttributeCancelResult extends ServerPacket {
    private final int _objId;
    private final byte _attribute;

    public ExBaseAttributeCancelResult(int objId, byte attribute) {
        _objId = objId;
        _attribute = attribute;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_BASE_ATTRIBUTE_CANCEL_RESULT);

        writeInt(0x01); // result
        writeInt(_objId);
        writeInt(_attribute);
    }

}
