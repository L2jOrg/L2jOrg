package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.model.clan.entry.PledgeApplicantInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Map;

/**
 * @author Sdw
 */
public class ExPledgeWaitingList extends ServerPacket {
    private final Map<Integer, PledgeApplicantInfo> pledgePlayerRecruitInfos;

    public ExPledgeWaitingList(int clanId) {
        pledgePlayerRecruitInfos = ClanEntryManager.getInstance().getApplicantListForClan(clanId);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_PLEDGE_WAITING_LIST);

        writeInt(pledgePlayerRecruitInfos.size());
        for (PledgeApplicantInfo recruitInfo : pledgePlayerRecruitInfos.values()) {
            writeInt(recruitInfo.getPlayerId());
            writeString(recruitInfo.getPlayerName());
            writeInt(recruitInfo.getClassId());
            writeInt(recruitInfo.getPlayerLvl());
        }
    }

}
