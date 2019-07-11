package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ExGetOnAirShip extends ServerPacket {
    private final int _playerId;
    private final int _airShipId;
    private final Location _pos;

    public ExGetOnAirShip(Player player, Creature ship) {
        _playerId = player.getObjectId();
        _airShipId = ship.getObjectId();
        _pos = player.getInVehiclePosition();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_GET_ON_AIR_SHIP);

        writeInt(_playerId);
        writeInt(_airShipId);
        writeInt(_pos.getX());
        writeInt(_pos.getY());
        writeInt(_pos.getZ());
    }

}
