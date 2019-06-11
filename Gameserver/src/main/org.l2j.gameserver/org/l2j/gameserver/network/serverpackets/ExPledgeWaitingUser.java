package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.clan.entry.PledgeApplicantInfo;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExPledgeWaitingUser extends IClientOutgoingPacket {
    private final PledgeApplicantInfo _pledgeRecruitInfo;

    public ExPledgeWaitingUser(PledgeApplicantInfo pledgeRecruitInfo) {
        _pledgeRecruitInfo = pledgeRecruitInfo;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_PLEDGE_WAITING_USER);

        writeInt(_pledgeRecruitInfo.getPlayerId());
        writeString(_pledgeRecruitInfo.getMessage());
    }

}