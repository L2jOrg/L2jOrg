package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
public class ExPledgeRecruitInfo extends ServerPacket {
    private final Clan _clan;

    public ExPledgeRecruitInfo(int clanId) {
        _clan = ClanTable.getInstance().getClan(clanId);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_PLEDGE_RECRUIT_INFO);

        final var subPledges = _clan.getAllSubPledges();
        writeString(_clan.getName());
        writeString(_clan.getLeaderName());
        writeInt(_clan.getLevel());
        writeInt(_clan.getMembersCount());
        writeInt(subPledges.length);
        for (var subPledge : subPledges) {
            writeInt(subPledge.getId());
            writeString(subPledge.getName());
        }
    }

}
