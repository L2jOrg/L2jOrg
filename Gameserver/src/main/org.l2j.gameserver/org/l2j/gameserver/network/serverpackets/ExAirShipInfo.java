package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2AirShipInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ExAirShipInfo extends IClientOutgoingPacket {
    // store some parameters, because they can be changed during broadcast
    private final L2AirShipInstance _ship;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _heading;
    private final int _moveSpeed;
    private final int _rotationSpeed;
    private final int _captain;
    private final int _helm;

    public ExAirShipInfo(L2AirShipInstance ship) {
        _ship = ship;
        _x = ship.getX();
        _y = ship.getY();
        _z = ship.getZ();
        _heading = ship.getHeading();
        _moveSpeed = (int) ship.getStat().getMoveSpeed();
        _rotationSpeed = (int) ship.getStat().getRotationSpeed();
        _captain = ship.getCaptainId();
        _helm = ship.getHelmObjectId();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_AIR_SHIP_INFO.writeId(packet);

        packet.putInt(_ship.getObjectId());
        packet.putInt(_x);
        packet.putInt(_y);
        packet.putInt(_z);
        packet.putInt(_heading);

        packet.putInt(_captain);
        packet.putInt(_moveSpeed);
        packet.putInt(_rotationSpeed);
        packet.putInt(_helm);
        if (_helm != 0) {
            // TODO: unhardcode these!
            packet.putInt(0x16e); // Controller X
            packet.putInt(0x00); // Controller Y
            packet.putInt(0x6b); // Controller Z
            packet.putInt(0x15c); // Captain X
            packet.putInt(0x00); // Captain Y
            packet.putInt(0x69); // Captain Z
        } else {
            packet.putInt(0x00);
            packet.putInt(0x00);
            packet.putInt(0x00);
            packet.putInt(0x00);
            packet.putInt(0x00);
            packet.putInt(0x00);
        }

        packet.putInt(_ship.getFuel());
        packet.putInt(_ship.getMaxFuel());
    }
}
