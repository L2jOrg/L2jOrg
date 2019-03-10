package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ExMoveToLocationAirShip extends IClientOutgoingPacket {
    private final int _objId;
    private final int _tx;
    private final int _ty;
    private final int _tz;
    private final int _x;
    private final int _y;
    private final int _z;

    public ExMoveToLocationAirShip(L2Character cha) {
        _objId = cha.getObjectId();
        _tx = cha.getXdestination();
        _ty = cha.getYdestination();
        _tz = cha.getZdestination();
        _x = cha.getX();
        _y = cha.getY();
        _z = cha.getZ();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_MOVE_TO_LOCATION_AIR_SHIP.writeId(packet);

        packet.putInt(_objId);
        packet.putInt(_tx);
        packet.putInt(_ty);
        packet.putInt(_tz);
        packet.putInt(_x);
        packet.putInt(_y);
        packet.putInt(_z);
    }
}
