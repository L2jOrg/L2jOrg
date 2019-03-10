package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.Location;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ObservationMode extends IClientOutgoingPacket {
    private final Location _loc;

    public ObservationMode(Location loc) {
        _loc = loc;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.OBSERVER_START.writeId(packet);

        packet.putInt(_loc.getX());
        packet.putInt(_loc.getY());
        packet.putInt(_loc.getZ());
        packet.putInt(0x00); // TODO: Find me
        packet.putInt(0xc0); // TODO: Find me
    }
}
