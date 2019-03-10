package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.mobius.gameserver.model.clan.entry.PledgeApplicantInfo;
import org.l2j.gameserver.mobius.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExPledgeWaitingListApplied extends IClientOutgoingPacket {
    private final PledgeApplicantInfo _pledgePlayerRecruitInfo;
    private final PledgeRecruitInfo _pledgeRecruitInfo;

    public ExPledgeWaitingListApplied(int clanId, int playerId) {
        _pledgePlayerRecruitInfo = ClanEntryManager.getInstance().getPlayerApplication(clanId, playerId);
        _pledgeRecruitInfo = ClanEntryManager.getInstance().getClanById(clanId);
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PLEDGE_WAITING_LIST_APPLIED.writeId(packet);

        packet.putInt(_pledgeRecruitInfo.getClan().getId());
        writeString(_pledgeRecruitInfo.getClan().getName(), packet);
        writeString(_pledgeRecruitInfo.getClan().getLeaderName(), packet);
        packet.putInt(_pledgeRecruitInfo.getClan().getLevel());
        packet.putInt(_pledgeRecruitInfo.getClan().getMembersCount());
        packet.putInt(_pledgeRecruitInfo.getKarma());
        writeString(_pledgeRecruitInfo.getInformation(), packet);
        writeString(_pledgePlayerRecruitInfo.getMessage(), packet);
    }
}
