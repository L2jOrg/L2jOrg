package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ObservationReturn extends ServerPacket {
    private final Location _loc;

    public ObservationReturn(Location loc) {
        _loc = loc;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.OBSERVER_END);

        writeInt(_loc.getX());
        writeInt(_loc.getY());
        writeInt(_loc.getZ());
    }

}
