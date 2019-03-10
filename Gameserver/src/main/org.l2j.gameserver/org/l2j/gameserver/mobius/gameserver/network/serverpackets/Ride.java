package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.Location;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class Ride extends IClientOutgoingPacket {
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.RIDE.writeId(packet);

        packet.putInt(_objectId);
        packet.putInt(_mounted);
        packet.putInt(_rideType);
        packet.putInt(_rideNpcId);
        packet.putInt(_loc.getX());
        packet.putInt(_loc.getY());
        packet.putInt(_loc.getZ());
    }
}
