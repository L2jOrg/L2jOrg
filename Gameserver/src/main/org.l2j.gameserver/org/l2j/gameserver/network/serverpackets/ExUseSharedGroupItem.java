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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_USE_SHARED_GROUP_ITEM);

        writeInt(_itemId);
        writeInt(_grpId);
        writeInt(_remainingTime);
        writeInt(_totalTime);
    }

}
