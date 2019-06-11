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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.OBSERVER_END);

        writeInt(_loc.getX());
        writeInt(_loc.getY());
        writeInt(_loc.getZ());
    }

}
