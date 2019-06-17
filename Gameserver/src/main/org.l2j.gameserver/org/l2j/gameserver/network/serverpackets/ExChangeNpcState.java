package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JIV
 */
public class ExChangeNpcState extends ServerPacket {
    private final int _objId;
    private final int _state;

    public ExChangeNpcState(int objId, int state) {
        _objId = objId;
        _state = state;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_CHANGE_NPC_STATE);

        writeInt(_objId);
        writeInt(_state);
    }

}
