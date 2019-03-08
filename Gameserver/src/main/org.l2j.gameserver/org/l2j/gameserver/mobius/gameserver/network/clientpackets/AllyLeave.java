package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;

import java.nio.ByteBuffer;

public final class AllyLeave extends IClientIncomingPacket
{
    @Override
    public void readImpl(ByteBuffer packet)
    {

    }

    @Override
    public void runImpl()
    {
        final L2PcInstance player = client.getActiveChar();
        if (player == null)
        {
            return;
        }

        if (player.getClan() == null)
        {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER_AND_CANNOT_PERFORM_THIS_ACTION);
            return;
        }
        if (!player.isClanLeader())
        {
            player.sendPacket(SystemMessageId.ONLY_THE_CLAN_LEADER_MAY_APPLY_FOR_WITHDRAWAL_FROM_THE_ALLIANCE);
            return;
        }
        final L2Clan clan = player.getClan();
        if (clan.getAllyId() == 0)
        {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS);
            return;
        }
        if (clan.getId() == clan.getAllyId())
        {
            player.sendPacket(SystemMessageId.ALLIANCE_LEADERS_CANNOT_WITHDRAW);
            return;
        }

        final long currentTime = System.currentTimeMillis();
        clan.setAllyId(0);
        clan.setAllyName(null);
        clan.changeAllyCrest(0, true);
        clan.setAllyPenaltyExpiryTime(currentTime + (Config.ALT_ALLY_JOIN_DAYS_WHEN_LEAVED * 86400000), L2Clan.PENALTY_TYPE_CLAN_LEAVED); // 24*60*60*1000 = 86400000
        clan.updateClanInDB();

        player.sendPacket(SystemMessageId.YOU_HAVE_WITHDRAWN_FROM_THE_ALLIANCE);
    }
}
