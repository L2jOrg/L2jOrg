package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.mobius.gameserver.enums.ClanEntryStatus;
import org.l2j.gameserver.mobius.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.clan.entry.PledgeApplicantInfo;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExPledgeRecruitApplyInfo;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExPledgeWaitingListAlarm;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class RequestPledgeWaitingApply extends IClientIncomingPacket {
    private int _karma;
    private int _clanId;
    private String _message;

    @Override
    public void readImpl(ByteBuffer packet) {
        _karma = packet.getInt();
        _clanId = packet.getInt();
        _message = readString(packet);
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if ((activeChar == null) || (activeChar.getClan() != null)) {
            return;
        }

        final L2Clan clan = ClanTable.getInstance().getClan(_clanId);
        if (clan == null) {
            return;
        }

        final PledgeApplicantInfo info = new PledgeApplicantInfo(activeChar.getObjectId(), activeChar.getName(), activeChar.getLevel(), _karma, _clanId, _message);
        if (ClanEntryManager.getInstance().addPlayerApplicationToClan(_clanId, info)) {
            client.sendPacket(new ExPledgeRecruitApplyInfo(ClanEntryStatus.WAITING));

            final L2PcInstance clanLeader = L2World.getInstance().getPlayer(clan.getLeaderId());
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
