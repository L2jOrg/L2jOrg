/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;

public final class RequestStopPledgeWar extends ClientPacket {
    private String _pledgeName;

    @Override
    public void readImpl() {
        _pledgeName = readString();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }
        final Clan playerClan = player.getClan();
        if (playerClan == null) {
            return;
        }

        final Clan clan = ClanTable.getInstance().getClanByName(_pledgeName);

        if (clan == null) {
            player.sendMessage("No such clan.");
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (!playerClan.isAtWarWith(clan.getId())) {
            player.sendMessage("You aren't at war with this clan.");
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Check if player who does the request has the correct rights to do it
        if (!player.hasClanPrivilege(ClanPrivilege.CL_PLEDGE_WAR)) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        for (ClanMember member : playerClan.getMembers()) {
            if ((member == null) || (member.getPlayerInstance() == null)) {
                continue;
            }
            if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(member.getPlayerInstance())) {
                player.sendPacket(SystemMessageId.A_CEASE_FIRE_DURING_A_CLAN_WAR_CAN_NOT_BE_CALLED_WHILE_MEMBERS_OF_YOUR_CLAN_ARE_ENGAGED_IN_BATTLE);
                return;
            }
        }

        // Reduce reputation.
        playerClan.takeReputationScore(500, true);

        ClanTable.getInstance().deleteClanWars(playerClan.getId(), clan.getId());

        for (Player member : playerClan.getOnlineMembers(0)) {
            member.broadcastUserInfo();
        }

        for (Player member : clan.getOnlineMembers(0)) {
            member.broadcastUserInfo();
        }
    }
}
