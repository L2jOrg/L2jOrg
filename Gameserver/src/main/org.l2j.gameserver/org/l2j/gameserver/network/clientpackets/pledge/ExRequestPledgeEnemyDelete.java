package org.l2j.gameserver.network.clientpackets.pledge;

import org.l2j.gameserver.data.database.data.ClanMember;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeEnemyInfoList;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;

public class ExRequestPledgeEnemyDelete extends ClientPacket {
    private int _clanId;
    @Override
    protected void readImpl() throws Exception {
        _clanId = readInt();
    }

    @Override
    protected void runImpl() {
        final Player player = client.getPlayer();
        if (player == null)
        {
            return;
        }
        final Clan playerClan = player.getClan();
        if (playerClan == null)
        {
            return;
        }

        final Clan clan = ClanTable.getInstance().getClan(_clanId);
        if (clan == null)
        {
            player.sendMessage("No such clan.");
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (!playerClan.isAtWarWith(clan.getId()))
        {
            player.sendMessage("You aren't at war with this clan.");
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Check if player who does the request has the correct rights to do it
        if (!player.hasClanPrivilege(ClanPrivilege.CL_PLEDGE_WAR))
        {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        for (ClanMember member : playerClan.getMembers())
        {
            if ((member == null) || (member.getPlayerInstance() == null))
            {
                continue;
            }
            if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(member.getPlayerInstance()))
            {
                player.sendPacket(SystemMessageId.A_CEASE_FIRE_DURING_A_CLAN_WAR_CAN_NOT_BE_CALLED_WHILE_MEMBERS_OF_YOUR_CLAN_ARE_ENGAGED_IN_BATTLE);
                return;
            }
        }

        // Reduce reputation.
        playerClan.takeReputationScore(500, true);
        ClanTable.getInstance().deleteClanWars(playerClan.getId(), clan.getId());
        for (Player member : playerClan.getOnlineMembers(0))
        {
            member.broadcastUserInfo();
        }

        for (Player member : clan.getOnlineMembers(0))
        {
            member.broadcastUserInfo();
        }
        player.sendPacket(new ExPledgeEnemyInfoList(player));
    }
}
