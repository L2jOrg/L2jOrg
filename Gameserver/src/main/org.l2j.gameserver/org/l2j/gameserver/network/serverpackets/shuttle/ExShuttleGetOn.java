package org.l2j.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.Shuttle;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExShuttleGetOn extends ServerPacket {
    private final int _playerObjectId;
    private final int _shuttleObjectId;
    private final Location _pos;

    public ExShuttleGetOn(Player player, Shuttle shuttle) {
        _playerObjectId = player.getObjectId();
        _shuttleObjectId = shuttle.getObjectId();
        _pos = player.getInVehiclePosition();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_GETON_SHUTTLE);

        writeInt(_playerObjectId);
        writeInt(_shuttleObjectId);
        writeInt(_pos.getX());
        writeInt(_pos.getY());
        writeInt(_pos.getZ());
    }

}
