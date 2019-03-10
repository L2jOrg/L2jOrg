package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class StopRotation extends IClientOutgoingPacket {
    private final int _charObjId;
    private final int _degree;
    private final int _speed;

    public StopRotation(int objectId, int degree, int speed) {
        _charObjId = objectId;
        _degree = degree;
        _speed = speed;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.FINISH_ROTATING.writeId(packet);

        packet.putInt(_charObjId);
        packet.putInt(_degree);
        packet.putInt(_speed);
        packet.putInt(0); // ?
    }
}
