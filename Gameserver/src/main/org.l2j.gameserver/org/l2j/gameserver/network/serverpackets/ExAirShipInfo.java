package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.AirShip;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ExAirShipInfo extends ServerPacket {
    // store some parameters, because they can be changed during broadcast
    private final AirShip _ship;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _heading;
    private final int _moveSpeed;
    private final int _rotationSpeed;
    private final int _captain;
    private final int _helm;

    public ExAirShipInfo(AirShip ship) {
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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_AIR_SHIP_INFO);

        writeInt(_ship.getObjectId());
        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
        writeInt(_heading);

        writeInt(_captain);
        writeInt(_moveSpeed);
        writeInt(_rotationSpeed);
        writeInt(_helm);
        if (_helm != 0) {
            // TODO: unhardcode these!
            writeInt(0x16e); // Controller X
            writeInt(0x00); // Controller Y
            writeInt(0x6b); // Controller Z
            writeInt(0x15c); // Captain X
            writeInt(0x00); // Captain Y
            writeInt(0x69); // Captain Z
        } else {
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
        }

        writeInt(_ship.getFuel());
        writeInt(_ship.getMaxFuel());
    }

}
