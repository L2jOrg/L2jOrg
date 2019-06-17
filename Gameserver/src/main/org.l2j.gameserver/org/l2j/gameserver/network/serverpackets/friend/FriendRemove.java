package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class FriendRemove extends ServerPacket {
    private final int _responce;
    private final String _charName;

    public FriendRemove(String charName, int responce) {
        _responce = responce;
        _charName = charName;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.FRIEND_REMOVE);

        writeInt(_responce);
        writeString(_charName);
    }

}
