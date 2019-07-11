package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ValidateLocation extends ServerPacket {
    private final int _charObjId;
    private final Location _loc;

    public ValidateLocation(WorldObject obj) {
        _charObjId = obj.getObjectId();
        _loc = obj.getLocation();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.VALIDATE_LOCATION);

        writeInt(_charObjId);
        writeInt(_loc.getX());
        writeInt(_loc.getY());
        writeInt(_loc.getZ());
        writeInt(_loc.getHeading());
        writeByte((byte) 0xFF); // TODO: Find me!
    }

}
