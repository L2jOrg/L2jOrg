package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2AirShipInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ExAirShipStopMove extends IClientOutgoingPacket {
    private final int _playerId;
    private final int _airShipId;
    private final int _x;
    private final int _y;
    private final int _z;

    public ExAirShipStopMove(L2PcInstance player, L2AirShipInstance ship, int x, int y, int z) {
        _playerId = player.getObjectId();
        _airShipId = ship.getObjectId();
        _x = x;
        _y = y;
        _z = z;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_MOVE_TO_LOCATION_AIR_SHIP.writeId(packet);

        packet.putInt(_airShipId);
        packet.putInt(_playerId);
        packet.putInt(_x);
        packet.putInt(_y);
        packet.putInt(_z);
    }
}