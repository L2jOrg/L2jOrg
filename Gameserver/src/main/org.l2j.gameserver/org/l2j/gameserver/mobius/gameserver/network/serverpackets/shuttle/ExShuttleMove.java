package org.l2j.gameserver.mobius.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2ShuttleInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class ExShuttleMove extends IClientOutgoingPacket {
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SUTTLE_MOVE.writeId(packet);

        packet.putInt(_shuttle.getObjectId());
        packet.putInt((int) _shuttle.getStat().getMoveSpeed());
        packet.putInt((int) _shuttle.getStat().getRotationSpeed());
        packet.putInt(_x);
        packet.putInt(_y);
        packet.putInt(_z);
    }
}
