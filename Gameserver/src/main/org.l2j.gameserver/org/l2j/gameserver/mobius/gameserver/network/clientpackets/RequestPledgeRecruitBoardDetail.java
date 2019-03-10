package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExPledgeRecruitBoardDetail;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class RequestPledgeRecruitBoardDetail extends IClientIncomingPacket {
    private int _clanId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _clanId = packet.getInt();
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
