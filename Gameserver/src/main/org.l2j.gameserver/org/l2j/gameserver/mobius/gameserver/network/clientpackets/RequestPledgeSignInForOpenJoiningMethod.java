package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.mobius.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.mobius.gameserver.instancemanager.FortManager;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.*;

import java.nio.ByteBuffer;

/**
 * @author Mobius
 */
public class RequestPledgeSignInForOpenJoiningMethod extends IClientIncomingPacket
{
    private int _clanId;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _clanId = packet.getInt();
        packet.getInt();
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }

        final PledgeRecruitInfo pledgeRecruitInfo = ClanEntryManager.getInstance().getClanById(_clanId);
        if (pledgeRecruitInfo != null)
        {
            final L2Clan clan = pledgeRecruitInfo.getClan();
            if ((clan != null) && (activeChar.getClan() == null))
            {
                activeChar.sendPacket(new JoinPledge(clan.getId()));

                // activeChar.setPowerGrade(9); // academy
                activeChar.setPowerGrade(5); // New member starts at 5, not confirmed.

                clan.addClanMember(activeChar);
                activeChar.setClanPrivileges(activeChar.getClan().getRankPrivs(activeChar.getPowerGrade()));
                activeChar.sendPacket(SystemMessageId.ENTERED_THE_CLAN);

                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_JOINED_THE_CLAN);
                sm.addString(activeChar.getName());
                clan.broadcastToOnlineMembers(sm);

                if (clan.getCastleId() > 0)
                {
                    CastleManager.getInstance().getCastleByOwner(clan).giveResidentialSkills(activeChar);
                }
                if (clan.getFortId() > 0)
                {
                    FortManager.getInstance().getFortByOwner(clan).giveResidentialSkills(activeChar);
                }
                activeChar.sendSkillList();

                clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListAdd(activeChar), activeChar);
                clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
                clan.broadcastToOnlineMembers(new ExPledgeCount(clan));

                // This activates the clan tab on the new member.
                PledgeShowMemberListAll.sendAllTo(activeChar);
                activeChar.setClanJoinExpiryTime(0);
                activeChar.broadcastUserInfo();

                ClanEntryManager.getInstance().removePlayerApplication(_clanId, activeChar.getObjectId());
            }
        }
    }
}