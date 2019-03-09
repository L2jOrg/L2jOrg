package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2ClanMember;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExPledgeCount;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.PledgeShowMemberListDelete;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestOustPledgeMember extends IClientIncomingPacket
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestOustPledgeMember.class);
    private String _target;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _target = readString(packet);
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }
        if (activeChar.getClan() == null)
        {
            client.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER_AND_CANNOT_PERFORM_THIS_ACTION);
            return;
        }
        if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_DISMISS))
        {
            client.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }
        if (activeChar.getName().equalsIgnoreCase(_target))
        {
            client.sendPacket(SystemMessageId.YOU_CANNOT_DISMISS_YOURSELF);
            return;
        }

        final L2Clan clan = activeChar.getClan();

        final L2ClanMember member = clan.getClanMember(_target);
        if (member == null)
        {
            LOGGER.warn("Target (" + _target + ") is not member of the clan");
            return;
        }
        if (member.isOnline() && member.getPlayerInstance().isInCombat())
        {
            client.sendPacket(SystemMessageId.A_CLAN_MEMBER_MAY_NOT_BE_DISMISSED_DURING_COMBAT);
            return;
        }

        // this also updates the database
        clan.removeClanMember(member.getObjectId(), System.currentTimeMillis() + (Config.ALT_CLAN_JOIN_DAYS * 86400000)); // 24*60*60*1000 = 86400000
        clan.setCharPenaltyExpiryTime(System.currentTimeMillis() + (Config.ALT_CLAN_JOIN_DAYS * 86400000)); // 24*60*60*1000 = 86400000
        clan.updateClanInDB();

        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_HAS_BEEN_EXPELLED);
        sm.addString(member.getName());
        clan.broadcastToOnlineMembers(sm);
        client.sendPacket(SystemMessageId.YOU_HAVE_SUCCEEDED_IN_EXPELLING_THE_CLAN_MEMBER);
        client.sendPacket(SystemMessageId.AFTER_A_CLAN_MEMBER_IS_DISMISSED_FROM_A_CLAN_THE_CLAN_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_ACCEPTING_A_NEW_MEMBER);

        // Remove the Player From the Member list
        clan.broadcastToOnlineMembers(new PledgeShowMemberListDelete(_target));
        clan.broadcastToOnlineMembers(new ExPledgeCount(clan));

        if (member.isOnline())
        {
            final L2PcInstance player = member.getPlayerInstance();
            player.sendPacket(SystemMessageId.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN_YOU_ARE_NOT_ALLOWED_TO_JOIN_ANOTHER_CLAN_FOR_24_HOURS);
        }
    }
}
