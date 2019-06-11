package org.l2j.gameserver.network.serverpackets.fishing;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

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
     * @param baitType - 0 = newbie, 1 = normal, 2 = night
     * @param baitLocation
     */
    public ExFishingStart(L2PcInstance player, int fishType, int baitType, ILocational baitLocation) {
        _player = player;
        _fishType = fishType;
        _baitType = baitType;
        _baitLocation = baitLocation;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_FISHING_START);
        writeInt(_player.getObjectId());
        writeByte((byte) _fishType);
        writeInt(_baitLocation.getX());
        writeInt(_baitLocation.getY());
        writeInt(_baitLocation.getZ());
        writeByte((byte) _baitType);
    }

}
