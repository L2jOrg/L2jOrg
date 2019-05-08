package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public class ExUseSharedGroupItem extends IClientOutgoingPacket {
    private final int _itemId;
    private final int _grpId;
    private final int _remainingTime;
    private final int _totalTime;

    public ExUseSharedGroupItem(int itemId, int grpId, long remainingTime, int totalTime) {
        _itemId = itemId;
        _grpId = grpId;
        _remainingTime = (int) (remainingTime / 1000);
        _totalTime = totalTime / 1000;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_USE_SHARED_GROUP_ITEM.writeId(packet);

        packet.putInt(_itemId);
        packet.putInt(_grpId);
        packet.putInt(_remainingTime);
        packet.putInt(_totalTime);
    }

    @Override
    protected int size(L2GameClient client) {
        return 25;
    }
}
