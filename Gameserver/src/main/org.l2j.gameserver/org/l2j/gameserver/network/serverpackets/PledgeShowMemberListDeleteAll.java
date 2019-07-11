package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

@StaticPacket
public class PledgeShowMemberListDeleteAll extends ServerPacket {
    public static final PledgeShowMemberListDeleteAll STATIC_PACKET = new PledgeShowMemberListDeleteAll();

    private PledgeShowMemberListDeleteAll() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PLEDGE_SHOW_MEMBER_LIST_DELETE_ALL);
    }

}
