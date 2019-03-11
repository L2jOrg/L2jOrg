package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class StartRotation extends IClientOutgoingPacket {
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.START_ROTATING.writeId(packet);

        packet.putInt(_charObjId);
        packet.putInt(_degree);
        packet.putInt(_side);
        packet.putInt(_speed);
    }
}
