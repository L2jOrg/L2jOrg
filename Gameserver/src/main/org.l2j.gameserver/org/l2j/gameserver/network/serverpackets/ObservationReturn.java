package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ObservationReturn extends IClientOutgoingPacket {
    private final Location _loc;

    public ObservationReturn(Location loc) {
        _loc = loc;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.OBSERVER_END.writeId(packet);

        packet.putInt(_loc.getX());
        packet.putInt(_loc.getY());
        packet.putInt(_loc.getZ());
    }
}
