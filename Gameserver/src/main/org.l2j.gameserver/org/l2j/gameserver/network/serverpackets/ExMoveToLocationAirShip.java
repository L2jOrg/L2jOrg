package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ExMoveToLocationAirShip extends ServerPacket {
    private final int _objId;
    private final int _tx;
    private final int _ty;
    private final int _tz;
    private final int _x;
    private final int _y;
    private final int _z;

    public ExMoveToLocationAirShip(Creature cha) {
        _objId = cha.getObjectId();
        _tx = cha.getXdestination();
        _ty = cha.getYdestination();
        _tz = cha.getZdestination();
        _x = cha.getX();
        _y = cha.getY();
        _z = cha.getZ();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_MOVE_TO_LOCATION_AIR_SHIP);

        writeInt(_objId);
        writeInt(_tx);
        writeInt(_ty);
        writeInt(_tz);
        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
    }

}
