package org.l2j.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.model.actor.instance.L2ShuttleInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExShuttleMove extends ServerPacket {
    private final L2ShuttleInstance _shuttle;
    private final int _x;
    private final int _y;
    private final int _z;

    public ExShuttleMove(L2ShuttleInstance shuttle, int x, int y, int z) {
        _shuttle = shuttle;
        _x = x;
        _y = y;
        _z = z;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_SUTTLE_MOVE);

        writeInt(_shuttle.getObjectId());
        writeInt((int) _shuttle.getStat().getMoveSpeed());
        writeInt((int) _shuttle.getStat().getRotationSpeed());
        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
    }

}
