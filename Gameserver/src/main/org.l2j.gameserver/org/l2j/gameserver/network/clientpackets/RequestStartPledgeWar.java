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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.enums.ClanWarState;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.ClanWar;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.util.Objects;

import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

public final class RequestStartPledgeWar extends ClientPacket {
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

        final Clan clanDeclaringWar = player.getClan();
        if (clanDeclaringWar == null) {
            return;
        }

        if ((clanDeclaringWar.getLevel() < 3) || (clanDeclaringWar.getMembersCount() < Config.ALT_CLAN_MEMBERS_FOR_WAR)) {
            client.sendPacket(getSystemMessage(SystemMessageId.A_CLAN_WAR_CAN_ONLY_BE_DECLARED_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE_AND_THE_NUMBER_OF_CLAN_MEMBERS_IS_15_OR_GREATER));
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        } else if (!player.hasClanPrivilege(ClanPrivilege.CL_PLEDGE_WAR)) {
            client.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        } else if (clanDeclaringWar.getWarCount() >= 30) {
            client.sendPacket(SystemMessageId.A_DECLARATION_OF_WAR_AGAINST_MORE_THAN_30_CLANS_CAN_T_BE_MADE_AT_THE_SAME_TIME);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final Clan clanDeclaredWar = ClanTable.getInstance().getClanByName(_pledgeName);
        if (clanDeclaredWar == null) {
            client.sendPacket(getSystemMessage(SystemMessageId.A_CLAN_WAR_CANNOT_BE_DECLARED_AGAINST_A_CLAN_THAT_DOES_NOT_EXIST));
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        } else if (clanDeclaredWar == clanDeclaringWar) {
            client.sendPacket(getSystemMessage(SystemMessageId.FOOL_YOU_CANNOT_DECLARE_WAR_AGAINST_YOUR_OWN_CLAN));
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        } else if ((clanDeclaringWar.getAllyId() == clanDeclaredWar.getAllyId()) && (clanDeclaringWar.getAllyId() != 0)) {
            client.sendPacket(getSystemMessage(SystemMessageId.A_DECLARATION_OF_CLAN_WAR_AGAINST_AN_ALLIED_CLAN_CAN_T_BE_MADE));
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        } else if ((clanDeclaredWar.getLevel() < 3) || (clanDeclaredWar.getMembersCount() < Config.ALT_CLAN_MEMBERS_FOR_WAR)) {
            client.sendPacket(getSystemMessage(SystemMessageId.A_CLAN_WAR_CAN_ONLY_BE_DECLARED_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE_AND_THE_NUMBER_OF_CLAN_MEMBERS_IS_15_OR_GREATER));
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        } else if (clanDeclaredWar.getDissolvingExpiryTime() > System.currentTimeMillis()) {
            client.sendPacket(getSystemMessage(SystemMessageId.A_CLAN_WAR_CAN_NOT_BE_DECLARED_AGAINST_A_CLAN_THAT_IS_BEING_DISSOLVED));
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final ClanWar clanWar = clanDeclaringWar.getWarWith(clanDeclaredWar.getId());
        if (clanWar != null) {
            if (clanWar.getClanWarState(clanDeclaringWar) == ClanWarState.WIN) {
                final SystemMessage sm = getSystemMessage(SystemMessageId.YOU_CAN_T_DECLARE_A_WAR_BECAUSE_THE_21_DAY_PERIOD_HASN_T_PASSED_AFTER_A_DEFEAT_DECLARATION_WITH_THE_S1_CLAN);
                sm.addString(clanDeclaredWar.getName());
                client.sendPacket(sm);
                client.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }

            final SystemMessage sm = getSystemMessage(SystemMessageId.YOU_HAVE_ALREADY_BEEN_AT_WAR_WITH_THE_S1_CLAN_5_DAYS_MUST_PASS_BEFORE_YOU_CAN_DECLARE_WAR_AGAIN);
            sm.addString(clanDeclaredWar.getName());
            client.sendPacket(sm);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final ClanWar newClanWar = new ClanWar(clanDeclaringWar, clanDeclaredWar);
        newClanWar.save();

        clanDeclaringWar.getMembers().stream().filter(Objects::nonNull).filter(ClanMember::isOnline).forEach(p -> p.getPlayerInstance().broadcastUserInfo(UserInfoType.CLAN));

        clanDeclaredWar.getMembers().stream().filter(Objects::nonNull).filter(ClanMember::isOnline).forEach(p -> p.getPlayerInstance().broadcastUserInfo(UserInfoType.CLAN));
    }
}
