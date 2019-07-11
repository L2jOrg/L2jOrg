package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class PledgeShowMemberListDelete extends ServerPacket {
    private final String _player;

    public PledgeShowMemberListDelete(String playerName) {
        _player = playerName;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PLEDGE_SHOW_MEMBER_LIST_DELETE);

        writeString(_player);
    }

}
