package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.model.clan.entry.PledgeApplicantInfo;
import org.l2j.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
public class ExPledgeWaitingListApplied extends ServerPacket {
    private final PledgeApplicantInfo _pledgePlayerRecruitInfo;
    private final PledgeRecruitInfo _pledgeRecruitInfo;

    public ExPledgeWaitingListApplied(int clanId, int playerId) {
        _pledgePlayerRecruitInfo = ClanEntryManager.getInstance().getPlayerApplication(clanId, playerId);
        _pledgeRecruitInfo = ClanEntryManager.getInstance().getClanById(clanId);
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_PLEDGE_WAITING_LIST_APPLIED);

        writeInt(_pledgeRecruitInfo.getClan().getId());
        writeString(_pledgeRecruitInfo.getClan().getName());
        writeString(_pledgeRecruitInfo.getClan().getLeaderName());
        writeInt(_pledgeRecruitInfo.getClan().getLevel());
        writeInt(_pledgeRecruitInfo.getClan().getMembersCount());
        writeInt(_pledgeRecruitInfo.getKarma());
        writeString(_pledgeRecruitInfo.getInformation());
        writeString(_pledgePlayerRecruitInfo.getMessage());
    }

}
