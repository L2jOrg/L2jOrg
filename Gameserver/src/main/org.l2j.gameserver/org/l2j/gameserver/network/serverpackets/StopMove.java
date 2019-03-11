package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class StopMove extends IClientOutgoingPacket {
    private final int _objectId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _heading;

    public StopMove(L2Character cha) {
        this(cha.getObjectId(), cha.getX(), cha.getY(), cha.getZ(), cha.getHeading());
    }

    /**
     * @param objectId
     * @param x
     * @param y
     * @param z
     * @param heading
     */
    public StopMove(int objectId, int x, int y, int z, int heading) {
        _objectId = objectId;
        _x = x;
        _y = y;
        _z = z;
        _heading = heading;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.STOP_MOVE.writeId(packet);

        packet.putInt(_objectId);
        packet.putInt(_x);
        packet.putInt(_y);
        packet.putInt(_z);
        packet.putInt(_heading);
    }
}
