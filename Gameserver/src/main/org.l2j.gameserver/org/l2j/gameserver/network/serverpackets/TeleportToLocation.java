package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class TeleportToLocation extends IClientOutgoingPacket {
    private final int _targetObjId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _heading;

    public TeleportToLocation(L2Object obj, int x, int y, int z, int heading) {
        _targetObjId = obj.getObjectId();
        _x = x;
        _y = y;
        _z = z;
        _heading = heading;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.TELEPORT_TO_LOCATION.writeId(packet);

        packet.putInt(_targetObjId);
        packet.putInt(_x);
        packet.putInt(_y);
        packet.putInt(_z);
        packet.putInt(0x00); // isValidation ??
        packet.putInt(_heading);
        packet.putInt(0x00); // Unknown
    }

    @Override
    protected int size(L2GameClient client) {
        return 33;
    }
}
