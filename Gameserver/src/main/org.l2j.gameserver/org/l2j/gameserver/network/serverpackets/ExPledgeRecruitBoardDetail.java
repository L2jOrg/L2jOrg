package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_PLEDGE_RECRUIT_BOARD_DETAIL);

        writeInt(_pledgeRecruitInfo.getClanId());
        writeInt(_pledgeRecruitInfo.getKarma());
        writeString(_pledgeRecruitInfo.getInformation());
        writeString(_pledgeRecruitInfo.getDetailedInformation());
        writeInt(_pledgeRecruitInfo.getApplicationType());
        writeInt(_pledgeRecruitInfo.getRecruitType());
    }

}
