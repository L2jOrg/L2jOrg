package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class StopRotation extends ServerPacket {
    private final int _charObjId;
    private final int _degree;
    private final int _speed;

    public StopRotation(int objectId, int degree, int speed) {
        _charObjId = objectId;
        _degree = degree;
        _speed = speed;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.FINISH_ROTATING);

        writeInt(_charObjId);
        writeInt(_degree);
        writeInt(_speed);
        writeInt(0); // ?
    }

}
