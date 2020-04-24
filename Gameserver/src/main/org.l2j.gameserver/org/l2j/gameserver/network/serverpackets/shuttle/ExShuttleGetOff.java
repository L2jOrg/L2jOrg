package org.l2j.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.Shuttle;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExShuttleGetOff extends ServerPacket {
    private final int _playerObjectId;
    private final int _shuttleObjectId;
    private final int _x;
    private final int _y;
    private final int _z;

    public ExShuttleGetOff(Player player, Shuttle shuttle, int x, int y, int z) {
        _playerObjectId = player.getObjectId();
        _shuttleObjectId = shuttle.getObjectId();
        _x = x;
        _y = y;
        _z = z;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_GETOFF_SHUTTLE);

        writeInt(_playerObjectId);
        writeInt(_shuttleObjectId);
        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
    }

}
