package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2j.gameserver.network.serverpackets.ExPledgeRecruitBoardDetail;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class RequestPledgeRecruitBoardDetail extends IClientIncomingPacket {
    private int _clanId;

    @Override
    public void readImpl() {
        _clanId = readInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final PledgeRecruitInfo pledgeRecruitInfo = ClanEntryManager.getInstance().getClanById(_clanId);
        if (pledgeRecruitInfo == null) {
            return;
        }

        client.sendPacket(new ExPledgeRecruitBoardDetail(pledgeRecruitInfo));
    }
}
