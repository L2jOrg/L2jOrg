package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author kerberos
 */
public class ExStopMoveAirShip extends IClientOutgoingPacket {
    private final int _objectId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _heading;

    public ExStopMoveAirShip(L2Character ship) {
        _objectId = ship.getObjectId();
        _x = ship.getX();
        _y = ship.getY();
        _z = ship.getZ();
        _heading = ship.getHeading();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_STOP_MOVE_AIR_SHIP.writeId(packet);

        packet.putInt(_objectId);
        packet.putInt(_x);
        packet.putInt(_y);
        packet.putInt(_z);
        packet.putInt(_heading);
    }
}
