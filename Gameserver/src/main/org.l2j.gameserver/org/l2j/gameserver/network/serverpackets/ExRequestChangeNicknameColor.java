package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Gnacik
 */
public class ExRequestChangeNicknameColor extends IClientOutgoingPacket {
    private final int _itemObjectId;

    public ExRequestChangeNicknameColor(int itemObjectId) {
        _itemObjectId = itemObjectId;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_CHANGE_NICKNAME_NCOLOR);

        writeInt(_itemObjectId);
    }

}
