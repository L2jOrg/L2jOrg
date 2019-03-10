package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.model.Location;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ValidateLocation extends IClientOutgoingPacket {
    private final int _charObjId;
    private final Location _loc;

    public ValidateLocation(L2Object obj) {
        _charObjId = obj.getObjectId();
        _loc = obj.getLocation();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.VALIDATE_LOCATION.writeId(packet);

        packet.putInt(_charObjId);
        packet.putInt(_loc.getX());
        packet.putInt(_loc.getY());
        packet.putInt(_loc.getZ());
        packet.putInt(_loc.getHeading());
        packet.put((byte) 0xFF); // TODO: Find me!
    }
}
