package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExPledgeRecruitBoardDetail extends IClientOutgoingPacket {
    final PledgeRecruitInfo _pledgeRecruitInfo;

    public ExPledgeRecruitBoardDetail(PledgeRecruitInfo pledgeRecruitInfo) {
        _pledgeRecruitInfo = pledgeRecruitInfo;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PLEDGE_RECRUIT_BOARD_DETAIL.writeId(packet);

        packet.putInt(_pledgeRecruitInfo.getClanId());
        packet.putInt(_pledgeRecruitInfo.getKarma());
        writeString(_pledgeRecruitInfo.getInformation(), packet);
        writeString(_pledgeRecruitInfo.getDetailedInformation(), packet);
        packet.putInt(_pledgeRecruitInfo.getApplicationType());
        packet.putInt(_pledgeRecruitInfo.getRecruitType());
    }
}
