package org.l2j.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class ExMoveToLocationInShuttle extends IClientOutgoingPacket {
    private final int _charObjId;
    private final int _airShipId;
    private final int _targetX;
    private final int _targetY;
    private final int _targetZ;
    private final int _fromX;
    private final int _fromY;
    private final int _fromZ;

    public ExMoveToLocationInShuttle(L2PcInstance player, int fromX, int fromY, int fromZ) {
        _charObjId = player.getObjectId();
        _airShipId = player.getShuttle().getObjectId();
        _targetX = player.getInVehiclePosition().getX();
        _targetY = player.getInVehiclePosition().getY();
        _targetZ = player.getInVehiclePosition().getZ();
        _fromX = fromX;
        _fromY = fromY;
        _fromZ = fromZ;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_MOVE_TO_LOCATION_IN_SUTTLE.writeId(packet);

        packet.putInt(_charObjId);
        packet.putInt(_airShipId);
        packet.putInt(_targetX);
        packet.putInt(_targetY);
        packet.putInt(_targetZ);
        packet.putInt(_fromX);
        packet.putInt(_fromY);
        packet.putInt(_fromZ);
    }

    @Override
    protected int size(L2GameClient client) {
        return 37;
    }
}
