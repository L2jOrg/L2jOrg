package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author JIV
 */
public class ExChangeNpcState extends IClientOutgoingPacket {
    private final int _objId;
    private final int _state;

    public ExChangeNpcState(int objId, int state) {
        _objId = objId;
        _state = state;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_CHANGE_NPC_STATE.writeId(packet);

        packet.putInt(_objId);
        packet.putInt(_state);
    }
}
