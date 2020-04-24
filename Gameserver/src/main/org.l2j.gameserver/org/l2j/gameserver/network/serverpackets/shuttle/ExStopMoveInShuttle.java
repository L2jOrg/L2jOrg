package org.l2j.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExStopMoveInShuttle extends ServerPacket {
    private final int _charObjId;
    private final int _boatId;
    private final Location _pos;
    private final int _heading;

    public ExStopMoveInShuttle(Player player, int boatId) {
        _charObjId = player.getObjectId();
        _boatId = boatId;
        _pos = player.getInVehiclePosition();
        _heading = player.getHeading();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_STOP_MOVE_LOCATION_IN_SHUTTLE);

        writeInt(_charObjId);
        writeInt(_boatId);
        writeInt(_pos.getX());
        writeInt(_pos.getY());
        writeInt(_pos.getZ());
        writeInt(_heading);
    }

}
