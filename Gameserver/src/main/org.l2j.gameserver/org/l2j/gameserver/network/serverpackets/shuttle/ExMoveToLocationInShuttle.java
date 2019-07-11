package org.l2j.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExMoveToLocationInShuttle extends ServerPacket {
    private final int _charObjId;
    private final int _airShipId;
    private final int _targetX;
    private final int _targetY;
    private final int _targetZ;
    private final int _fromX;
    private final int _fromY;
    private final int _fromZ;

    public ExMoveToLocationInShuttle(Player player, int fromX, int fromY, int fromZ) {
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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_MOVE_TO_LOCATION_IN_SUTTLE);

        writeInt(_charObjId);
        writeInt(_airShipId);
        writeInt(_targetX);
        writeInt(_targetY);
        writeInt(_targetZ);
        writeInt(_fromX);
        writeInt(_fromY);
        writeInt(_fromZ);
    }

}
