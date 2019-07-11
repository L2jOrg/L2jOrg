package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ExGetOffAirShip extends ServerPacket {
    private final int _playerId;
    private final int _airShipId;
    private final int _x;
    private final int _y;
    private final int _z;

    public ExGetOffAirShip(Creature player, Creature ship, int x, int y, int z) {
        _playerId = player.getObjectId();
        _airShipId = ship.getObjectId();
        _x = x;
        _y = y;
        _z = z;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_GET_OFF_AIR_SHIP);

        writeInt(_playerId);
        writeInt(_airShipId);
        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
    }

}
