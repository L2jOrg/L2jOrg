package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.ClanEntryStatus;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.ExPledgeRecruitApplyInfo;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class RequestPledgeRecruitApplyInfo extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final ClanEntryStatus status;

        if ((activeChar.getClan() != null) && activeChar.isClanLeader() && ClanEntryManager.getInstance().isClanRegistred(activeChar.getClanId())) {
            status = ClanEntryStatus.ORDERED;
        } else if ((activeChar.getClan() == null) && (ClanEntryManager.getInstance().isPlayerRegistred(activeChar.getObjectId()))) {
            status = ClanEntryStatus.WAITING;
        } else {
            status = ClanEntryStatus.DEFAULT;
        }

        activeChar.sendPacket(new ExPledgeRecruitApplyInfo(status));
    }
}
