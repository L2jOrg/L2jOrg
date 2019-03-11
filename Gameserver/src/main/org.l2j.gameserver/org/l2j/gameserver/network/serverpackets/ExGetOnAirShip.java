package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ExGetOnAirShip extends IClientOutgoingPacket {
    private final int _playerId;
    private final int _airShipId;
    private final Location _pos;

    public ExGetOnAirShip(L2PcInstance player, L2Character ship) {
        _playerId = player.getObjectId();
        _airShipId = ship.getObjectId();
        _pos = player.getInVehiclePosition();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_GET_ON_AIR_SHIP.writeId(packet);

        packet.putInt(_playerId);
        packet.putInt(_airShipId);
        packet.putInt(_pos.getX());
        packet.putInt(_pos.getY());
        packet.putInt(_pos.getZ());
    }
}
