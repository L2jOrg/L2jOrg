package org.l2j.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.model.actor.instance.Shuttle;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExShuttleMove extends ServerPacket {
    private final Shuttle _shuttle;
    private final int _x;
    private final int _y;
    private final int _z;

    public ExShuttleMove(Shuttle shuttle, int x, int y, int z) {
        _shuttle = shuttle;
        _x = x;
        _y = y;
        _z = z;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_SUTTLE_MOVE);

        writeInt(_shuttle.getObjectId());
        writeInt((int) _shuttle.getStats().getMoveSpeed());
        writeInt((int) _shuttle.getStats().getRotationSpeed());
        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
    }

}
