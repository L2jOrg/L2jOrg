package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class AutoAttackStop extends IClientOutgoingPacket {
    private final int _targetObjId;

    /**
     * @param targetObjId
     */
    public AutoAttackStop(int targetObjId) {
        _targetObjId = targetObjId;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.AUTO_ATTACK_STOP);

        writeInt(_targetObjId);
    }

}
