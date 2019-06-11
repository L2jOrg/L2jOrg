package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ObservationMode extends IClientOutgoingPacket {
    private final Location _loc;

    public ObservationMode(Location loc) {
        _loc = loc;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.OBSERVER_START);

        writeInt(_loc.getX());
        writeInt(_loc.getY());
        writeInt(_loc.getZ());
        writeInt(0x00); // TODO: Find me
        writeInt(0xc0); // TODO: Find me
    }

}
