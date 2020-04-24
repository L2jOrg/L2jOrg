package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.enums.ClanEntryStatus;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.clan.entry.PledgeApplicantInfo;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExPledgeRecruitApplyInfo;
import org.l2j.gameserver.network.serverpackets.ExPledgeWaitingListAlarm;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.world.World;

/**
 * @author Sdw
 */
public class RequestPledgeWaitingApply extends ClientPacket {
    private int _karma;
    private int _clanId;
    private String _message;

    @Override
    public void readImpl() {
        _karma = readInt();
        _clanId = readInt();
        _message = readString();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if ((activeChar == null) || (activeChar.getClan() != null)) {
            return;
        }

        final Clan clan = ClanTable.getInstance().getClan(_clanId);
        if (clan == null) {
            return;
        }

        final PledgeApplicantInfo info = new PledgeApplicantInfo(activeChar.getObjectId(), activeChar.getName(), activeChar.getLevel(), _karma, _clanId, _message);
        if (ClanEntryManager.getInstance().addPlayerApplicationToClan(_clanId, info)) {
            client.sendPacket(new ExPledgeRecruitApplyInfo(ClanEntryStatus.WAITING));

            final Player clanLeader = World.getInstance().findPlayer(clan.getLeaderId());
            if (clanLeader != null) {
                clanLeader.sendPacket(ExPledgeWaitingListAlarm.STATIC_PACKET);
            }
        } else {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_MAY_APPLY_FOR_ENTRY_AFTER_S1_MINUTE_S_DUE_TO_CANCELLING_YOUR_APPLICATION);
            sm.addLong(ClanEntryManager.getInstance().getPlayerLockTime(activeChar.getObjectId()));
            client.sendPacket(sm);
        }
    }
}
