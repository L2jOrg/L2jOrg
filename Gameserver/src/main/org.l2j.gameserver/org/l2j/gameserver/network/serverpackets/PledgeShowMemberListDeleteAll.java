package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

@StaticPacket
public class PledgeShowMemberListDeleteAll extends IClientOutgoingPacket {
    public static final PledgeShowMemberListDeleteAll STATIC_PACKET = new PledgeShowMemberListDeleteAll();

    private PledgeShowMemberListDeleteAll() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PLEDGE_SHOW_MEMBER_LIST_DELETE_ALL.writeId(packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return 5;
    }
}
