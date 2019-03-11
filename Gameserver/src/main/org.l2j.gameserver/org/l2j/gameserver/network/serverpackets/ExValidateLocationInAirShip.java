package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * update 27.8.10
 *
 * @author kerberos JIV
 */
public class ExValidateLocationInAirShip extends IClientOutgoingPacket {
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_VALIDATE_LOCATION_IN_AIR_SHIP.writeId(packet);

        packet.putInt(_activeChar.getObjectId());
        packet.putInt(_shipId);
        packet.putInt(_loc.getX());
        packet.putInt(_loc.getY());
        packet.putInt(_loc.getZ());
        packet.putInt(_heading);
    }
}
