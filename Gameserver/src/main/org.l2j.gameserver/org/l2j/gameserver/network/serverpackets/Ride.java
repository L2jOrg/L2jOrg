package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class Ride extends ServerPacket {
    private final int _objectId;
    private final int _mounted;
    private final int _rideType;
    private final int _rideNpcId;
    private final Location _loc;

    public Ride(L2PcInstance player) {
        _objectId = player.getObjectId();
        _mounted = player.isMounted() ? 1 : 0;
        _rideType = player.getMountType().ordinal();
        _rideNpcId = player.getMountNpcId() + 1000000;
        _loc = player.getLocation();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.RIDE);

        writeInt(_objectId);
        writeInt(_mounted);
        writeInt(_rideType);
        writeInt(_rideNpcId);
        writeInt(_loc.getX());
        writeInt(_loc.getY());
        writeInt(_loc.getZ());
    }

}
