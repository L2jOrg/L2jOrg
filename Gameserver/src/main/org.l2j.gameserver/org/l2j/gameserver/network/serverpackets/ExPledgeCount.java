package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author UnAfraid
 */
public class ExPledgeCount extends ServerPacket {
    private final int _count;

    public ExPledgeCount(Clan clan) {
        _count = clan.getOnlineMembersCount();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_PLEDGE_COUNT);

        writeInt(_count);
    }

}
