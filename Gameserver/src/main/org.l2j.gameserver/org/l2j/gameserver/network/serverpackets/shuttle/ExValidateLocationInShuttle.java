package org.l2j.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class ExValidateLocationInShuttle extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;
    private final int _shipId;
    private final int _heading;
    private final Location _loc;

    public ExValidateLocationInShuttle(L2PcInstance player) {
        _activeChar = player;
        _shipId = _activeChar.getShuttle().getObjectId();
        _loc = player.getInVehiclePosition();
        _heading = player.getHeading();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_VALIDATE_LOCATION_IN_SHUTTLE.writeId(packet);

        packet.putInt(_activeChar.getObjectId());
        packet.putInt(_shipId);
        packet.putInt(_loc.getX());
        packet.putInt(_loc.getY());
        packet.putInt(_loc.getZ());
        packet.putInt(_heading);
    }

    @Override
    protected int size(L2GameClient client) {
        return 29;
    }
}
