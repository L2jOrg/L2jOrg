package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author UnAfraid
 */
public class ExPledgeCount extends ServerPacket {
    private final int _count;

    public ExPledgeCount(L2Clan clan) {
        _count = clan.getOnlineMembersCount();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_PLEDGE_COUNT);

        writeInt(_count);
    }

}
