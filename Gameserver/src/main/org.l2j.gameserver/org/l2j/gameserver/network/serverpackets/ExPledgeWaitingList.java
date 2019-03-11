package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.model.clan.entry.PledgeApplicantInfo;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * @author Sdw
 */
public class ExPledgeWaitingList extends IClientOutgoingPacket {
    private final Map<Integer, PledgeApplicantInfo> pledgePlayerRecruitInfos;

    public ExPledgeWaitingList(int clanId) {
        pledgePlayerRecruitInfos = ClanEntryManager.getInstance().getApplicantListForClan(clanId);
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PLEDGE_WAITING_LIST.writeId(packet);

        packet.putInt(pledgePlayerRecruitInfos.size());
        for (PledgeApplicantInfo recruitInfo : pledgePlayerRecruitInfos.values()) {
            packet.putInt(recruitInfo.getPlayerId());
            writeString(recruitInfo.getPlayerName(), packet);
            packet.putInt(recruitInfo.getClassId());
            packet.putInt(recruitInfo.getPlayerLvl());
        }
    }
}
