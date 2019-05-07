package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class FriendRemove extends IClientOutgoingPacket {
    private final int _responce;
    private final String _charName;

    public FriendRemove(String charName, int responce) {
        _responce = responce;
        _charName = charName;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.FRIEND_REMOVE.writeId(packet);

        packet.putInt(_responce);
        writeString(_charName, packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return 11 + _charName.length() * 2;
    }
}
