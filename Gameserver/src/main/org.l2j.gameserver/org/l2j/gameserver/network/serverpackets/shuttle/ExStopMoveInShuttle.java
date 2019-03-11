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
public class ExStopMoveInShuttle extends IClientOutgoingPacket {
    private final int _charObjId;
    private final int _boatId;
    private final Location _pos;
    private final int _heading;

    public ExStopMoveInShuttle(L2PcInstance player, int boatId) {
        _charObjId = player.getObjectId();
        _boatId = boatId;
        _pos = player.getInVehiclePosition();
        _heading = player.getHeading();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_STOP_MOVE_IN_SHUTTLE.writeId(packet);

        packet.putInt(_charObjId);
        packet.putInt(_boatId);
        packet.putInt(_pos.getX());
        packet.putInt(_pos.getY());
        packet.putInt(_pos.getZ());
        packet.putInt(_heading);
    }
}
