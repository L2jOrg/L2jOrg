package org.l2j.gameserver.mobius.gameserver.network.serverpackets.fishing;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author -Wooden-
 */
public class ExFishingStart extends IClientOutgoingPacket {
    private final L2PcInstance _player;
    private final int _fishType;
    private final int _baitType;
    private final ILocational _baitLocation;

    /**
     * @param player
     * @param fishType
     * @param baitType     - 0 = newbie, 1 = normal, 2 = night
     * @param baitLocation
     */
    public ExFishingStart(L2PcInstance player, int fishType, int baitType, ILocational baitLocation) {
        _player = player;
        _fishType = fishType;
        _baitType = baitType;
        _baitLocation = baitLocation;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_FISHING_START.writeId(packet);
        packet.putInt(_player.getObjectId());
        packet.put((byte) _fishType);
        packet.putInt(_baitLocation.getX());
        packet.putInt(_baitLocation.getY());
        packet.putInt(_baitLocation.getZ());
        packet.put((byte) _baitType);
    }
}
