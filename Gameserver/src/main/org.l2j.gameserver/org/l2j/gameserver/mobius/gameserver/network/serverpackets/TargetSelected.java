package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class TargetSelected extends IClientOutgoingPacket {
    private final int _objectId;
    private final int _targetObjId;
    private final int _x;
    private final int _y;
    private final int _z;

    /**
     * @param objectId
     * @param targetId
     * @param x
     * @param y
     * @param z
     */
    public TargetSelected(int objectId, int targetId, int x, int y, int z) {
        _objectId = objectId;
        _targetObjId = targetId;
        _x = x;
        _y = y;
        _z = z;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.TARGET_SELECTED.writeId(packet);

        packet.putInt(_objectId);
        packet.putInt(_targetObjId);
        packet.putInt(_x);
        packet.putInt(_y);
        packet.putInt(_z);
        packet.putInt(0x00); // ?
    }
}
