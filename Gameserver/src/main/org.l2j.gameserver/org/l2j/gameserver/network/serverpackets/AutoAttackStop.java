package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class AutoAttackStop extends ServerPacket {
    private final int _targetObjId;

    /**
     * @param targetObjId
     */
    public AutoAttackStop(int targetObjId) {
        _targetObjId = targetObjId;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.AUTO_ATTACK_STOP);

        writeInt(_targetObjId);
    }

}
