package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class AutoAttackStart extends ServerPacket {
    private final int _targetObjId;

    /**
     * @param targetId
     */
    public AutoAttackStart(int targetId) {
        _targetObjId = targetId;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.AUTO_ATTACK_START);

        writeInt(_targetObjId);
    }

}
