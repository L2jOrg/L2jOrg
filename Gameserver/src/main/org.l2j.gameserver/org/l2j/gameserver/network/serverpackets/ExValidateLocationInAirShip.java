package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * update 27.8.10
 *
 * @author kerberos JIV
 */
public class ExValidateLocationInAirShip extends ServerPacket {
    private final L2PcInstance _activeChar;
    private final int _shipId;
    private final int _heading;
    private final Location _loc;

    public ExValidateLocationInAirShip(L2PcInstance player) {
        _activeChar = player;
        _shipId = _activeChar.getAirShip().getObjectId();
        _loc = player.getInVehiclePosition();
        _heading = player.getHeading();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_VALIDATE_LOCATION_IN_AIR_SHIP);

        writeInt(_activeChar.getObjectId());
        writeInt(_shipId);
        writeInt(_loc.getX());
        writeInt(_loc.getY());
        writeInt(_loc.getZ());
        writeInt(_heading);
    }

}
