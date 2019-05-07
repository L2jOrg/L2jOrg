package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class AutoAttackStart extends IClientOutgoingPacket {
    private final int _targetObjId;

    /**
     * @param targetId
     */
    public AutoAttackStart(int targetId) {
        _targetObjId = targetId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.AUTO_ATTACK_START.writeId(packet);

        packet.putInt(_targetObjId);
    }

    @Override
    protected int size(L2GameClient client) {
        return 9;
    }
}
