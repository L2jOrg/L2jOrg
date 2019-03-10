package org.l2j.gameserver.mobius.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.mobius.gameserver.model.Location;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2ShuttleInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class ExShuttleGetOn extends IClientOutgoingPacket {
    private final int _playerObjectId;
    private final int _shuttleObjectId;
    private final Location _pos;

    public ExShuttleGetOn(L2PcInstance player, L2ShuttleInstance shuttle) {
        _playerObjectId = player.getObjectId();
        _shuttleObjectId = shuttle.getObjectId();
        _pos = player.getInVehiclePosition();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SUTTLE_GET_ON.writeId(packet);

        packet.putInt(_playerObjectId);
        packet.putInt(_shuttleObjectId);
        packet.putInt(_pos.getX());
        packet.putInt(_pos.getY());
        packet.putInt(_pos.getZ());
    }
}
