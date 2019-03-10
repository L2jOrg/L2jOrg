package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_BASE_ATTRIBUTE_CANCEL_RESULT.writeId(packet);

        packet.putInt(0x01); // result
        packet.putInt(_objId);
        packet.putInt(_attribute);
    }
}
