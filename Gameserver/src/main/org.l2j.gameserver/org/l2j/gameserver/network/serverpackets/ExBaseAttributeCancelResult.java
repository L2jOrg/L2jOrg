package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author JIV
 */
public class ExBaseAttributeCancelResult extends IClientOutgoingPacket {
    private final int _objId;
    private final byte _attribute;

    public ExBaseAttributeCancelResult(int objId, byte attribute) {
        _objId = objId;
        _attribute = attribute;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_BASE_ATTRIBUTE_CANCEL_RESULT);

        writeInt(0x01); // result
        writeInt(_objId);
        writeInt(_attribute);
    }

}
