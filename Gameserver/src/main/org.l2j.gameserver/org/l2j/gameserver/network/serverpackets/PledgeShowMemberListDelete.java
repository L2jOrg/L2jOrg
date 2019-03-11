package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class PledgeShowMemberListDelete extends IClientOutgoingPacket {
    private final String _player;

    public PledgeShowMemberListDelete(String playerName) {
        _player = playerName;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PLEDGE_SHOW_MEMBER_LIST_DELETE.writeId(packet);

        writeString(_player, packet);
    }
}
