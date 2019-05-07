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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PLEDGE_RECRUIT_BOARD_DETAIL.writeId(packet);

        packet.putInt(_pledgeRecruitInfo.getClanId());
        packet.putInt(_pledgeRecruitInfo.getKarma());
        writeString(_pledgeRecruitInfo.getInformation(), packet);
        writeString(_pledgeRecruitInfo.getDetailedInformation(), packet);
        packet.putInt(_pledgeRecruitInfo.getApplicationType());
        packet.putInt(_pledgeRecruitInfo.getRecruitType());
    }

    @Override
    protected int size(L2GameClient client) {
        return 25 + (_pledgeRecruitInfo.getInformation().length() + _pledgeRecruitInfo.getDetailedInformation().length()) * 2;
    }
}
