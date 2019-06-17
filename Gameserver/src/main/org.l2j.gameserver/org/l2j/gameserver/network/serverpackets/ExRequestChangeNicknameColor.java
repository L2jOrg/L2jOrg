package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Gnacik
 */
public class ExRequestChangeNicknameColor extends ServerPacket {
    private final int _itemObjectId;

    public ExRequestChangeNicknameColor(int itemObjectId) {
        _itemObjectId = itemObjectId;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_CHANGE_NICKNAME_NCOLOR);

        writeInt(_itemObjectId);
    }

}
