package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author kerberos
 */
public class ExStopMoveAirShip extends ServerPacket {
    private final int _objectId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _heading;

    public ExStopMoveAirShip(Creature ship) {
        _objectId = ship.getObjectId();
        _x = ship.getX();
        _y = ship.getY();
        _z = ship.getZ();
        _heading = ship.getHeading();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_STOP_MOVE_AIR_SHIP);

        writeInt(_objectId);
        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
        writeInt(_heading);
    }

}
