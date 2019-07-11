package org.l2j.gameserver.network.serverpackets.fishing;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author -Wooden-
 */
public class ExFishingStart extends ServerPacket {
    private final Player _player;
    private final int _fishType;
    private final int _baitType;
    private final ILocational _baitLocation;

    /**
     * @param player
     * @param fishType
     * @param baitType - 0 = newbie, 1 = normal, 2 = night
     * @param baitLocation
     */
    public ExFishingStart(Player player, int fishType, int baitType, ILocational baitLocation) {
        _player = player;
        _fishType = fishType;
        _baitType = baitType;
        _baitLocation = baitLocation;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_FISHING_START);
        writeInt(_player.getObjectId());
        writeByte((byte) _fishType);
        writeInt(_baitLocation.getX());
        writeInt(_baitLocation.getY());
        writeInt(_baitLocation.getZ());
        writeByte((byte) _baitType);
    }

}
