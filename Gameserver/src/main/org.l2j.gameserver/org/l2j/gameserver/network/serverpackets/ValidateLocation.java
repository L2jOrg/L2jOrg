package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ValidateLocation extends IClientOutgoingPacket {
    private final int _charObjId;
    private final Location _loc;

    public ValidateLocation(L2Object obj) {
        _charObjId = obj.getObjectId();
        _loc = obj.getLocation();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.VALIDATE_LOCATION);

        writeInt(_charObjId);
        writeInt(_loc.getX());
        writeInt(_loc.getY());
        writeInt(_loc.getZ());
        writeInt(_loc.getHeading());
        writeByte((byte) 0xFF); // TODO: Find me!
    }

}
