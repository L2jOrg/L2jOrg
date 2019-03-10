package org.l2j.gameserver.mobius.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2ShuttleInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class ExShuttleGetOff extends IClientOutgoingPacket {
    private final int _playerObjectId;
    private final int _shuttleObjectId;
    private final int _x;
    private final int _y;
    private final int _z;

    public ExShuttleGetOff(L2PcInstance player, L2ShuttleInstance shuttle, int x, int y, int z) {
        _playerObjectId = player.getObjectId();
        _shuttleObjectId = shuttle.getObjectId();
        _x = x;
        _y = y;
        _z = z;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SUTTLE_GET_OFF.writeId(packet);

        packet.putInt(_playerObjectId);
        packet.putInt(_shuttleObjectId);
        packet.putInt(_x);
        packet.putInt(_y);
        packet.putInt(_z);
    }
}
