package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class TargetSelected extends ServerPacket {
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
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.TARGET_SELECTED);

        writeInt(_objectId);
        writeInt(_targetObjId);
        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
        writeInt(0x00); // ?
    }

}
