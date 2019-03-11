package org.l2j.gameserver.network.serverpackets.sayune;

import org.l2j.gameserver.enums.SayuneType;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class ExFlyMoveBroadcast extends IClientOutgoingPacket {
    private final int _objectId;
    private final int _mapId;
    private final ILocational _currentLoc;
    private final ILocational _targetLoc;
    private final SayuneType _type;

    public ExFlyMoveBroadcast(L2PcInstance activeChar, SayuneType type, int mapId, ILocational targetLoc) {
        _objectId = activeChar.getObjectId();
        _type = type;
        _mapId = mapId;
        _currentLoc = activeChar;
        _targetLoc = targetLoc;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_FLY_MOVE_BROADCAST.writeId(packet);

        packet.putInt(_objectId);

        packet.putInt(_type.ordinal());
        packet.putInt(_mapId);

        packet.putInt(_targetLoc.getX());
        packet.putInt(_targetLoc.getY());
        packet.putInt(_targetLoc.getZ());
        packet.putInt(0x00); // ?
        packet.putInt(_currentLoc.getX());
        packet.putInt(_currentLoc.getY());
        packet.putInt(_currentLoc.getZ());
    }
}
