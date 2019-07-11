package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_CHANGE_NPC_STATE);

        writeInt(_objId);
        writeInt(_state);
    }

}
