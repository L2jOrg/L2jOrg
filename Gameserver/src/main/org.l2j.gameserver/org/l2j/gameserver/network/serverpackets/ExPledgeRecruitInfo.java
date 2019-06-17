package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
public class ExPledgeRecruitInfo extends ServerPacket {
    private final L2Clan _clan;

    public ExPledgeRecruitInfo(int clanId) {
        _clan = ClanTable.getInstance().getClan(clanId);
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_PLEDGE_RECRUIT_INFO);

        final L2Clan.SubPledge[] subPledges = _clan.getAllSubPledges();
        writeString(_clan.getName());
        writeString(_clan.getLeaderName());
        writeInt(_clan.getLevel());
        writeInt(_clan.getMembersCount());
        writeInt(subPledges.length);
        for (L2Clan.SubPledge subPledge : subPledges) {
            writeInt(subPledge.getId());
            writeString(subPledge.getName());
        }
    }

}
