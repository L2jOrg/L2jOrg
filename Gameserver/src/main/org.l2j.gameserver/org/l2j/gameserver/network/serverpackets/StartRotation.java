package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class StartRotation extends ServerPacket {
    private final int _charObjId;
    private final int _degree;
    private final int _side;
    private final int _speed;

    public StartRotation(int objectId, int degree, int side, int speed) {
        _charObjId = objectId;
        _degree = degree;
        _side = side;
        _speed = speed;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.START_ROTATING);

        writeInt(_charObjId);
        writeInt(_degree);
        writeInt(_side);
        writeInt(_speed);
    }

}
