package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.clan.entry.PledgeApplicantInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author Sdw
 */
public class ExPledgeWaitingUser extends ServerPacket {
    private final PledgeApplicantInfo _pledgeRecruitInfo;

    public ExPledgeWaitingUser(PledgeApplicantInfo pledgeRecruitInfo) {
        _pledgeRecruitInfo = pledgeRecruitInfo;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_PLEDGE_WAITING_USER);

        writeInt(_pledgeRecruitInfo.getPlayerId());
        writeString(_pledgeRecruitInfo.getMessage());
    }

}