package org.l2j.gameserver.mobius.gameserver.network.serverpackets.sayune;

import org.l2j.gameserver.mobius.gameserver.enums.SayuneType;
import org.l2j.gameserver.mobius.gameserver.model.SayuneEntry;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author UnAfraid
 */
public class ExFlyMove extends IClientOutgoingPacket {
    private final int _objectId;
    private final SayuneType _type;
    private final int _mapId;
    private final List<SayuneEntry> _locations;

    public ExFlyMove(L2PcInstance activeChar, SayuneType type, int mapId, List<SayuneEntry> locations) {
        _objectId = activeChar.getObjectId();
        _type = type;
        _mapId = mapId;
        _locations = locations;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_FLY_MOVE.writeId(packet);

        packet.putInt(_objectId);

        packet.putInt(_type.ordinal());
        packet.putInt(0x00); // ??
        packet.putInt(_mapId);

        packet.putInt(_locations.size());
        for (SayuneEntry loc : _locations) {
            packet.putInt(loc.getId());
            packet.putInt(0x00); // ??
            packet.putInt(loc.getX());
            packet.putInt(loc.getY());
            packet.putInt(loc.getZ());
        }
    }
}
