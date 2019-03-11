package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * update 27.8.10
 *
 * @author kerberos, JIV
 */
public class ExStopMoveInAirShip extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;
    private final int _shipObjId;
    private final int _h;
    private final Location _loc;

    public ExStopMoveInAirShip(L2PcInstance player, int shipObjId) {
        _activeChar = player;
        _shipObjId = shipObjId;
        _h = player.getHeading();
        _loc = player.getInVehiclePosition();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_STOP_MOVE_IN_AIR_SHIP.writeId(packet);

        packet.putInt(_activeChar.getObjectId());
        packet.putInt(_shipObjId);
        packet.putInt(_loc.getX());
        packet.putInt(_loc.getY());
        packet.putInt(_loc.getZ());
        packet.putInt(_h);
    }
}
