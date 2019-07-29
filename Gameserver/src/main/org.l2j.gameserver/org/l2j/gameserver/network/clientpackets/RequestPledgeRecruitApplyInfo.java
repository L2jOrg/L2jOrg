package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.ClanEntryStatus;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExPledgeRecruitApplyInfo;

/**
 * @author Sdw
 */
public class RequestPledgeRecruitApplyInfo extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
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
