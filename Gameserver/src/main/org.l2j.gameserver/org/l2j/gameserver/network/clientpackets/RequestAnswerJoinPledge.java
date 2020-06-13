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

import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExPledgeCount;
import org.l2j.gameserver.network.serverpackets.JoinPledge;
import org.l2j.gameserver.network.serverpackets.PledgeShowMemberListAdd;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowInfoUpdate;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowMemberListAll;

public final class RequestAnswerJoinPledge extends ClientPacket {
    private int _answer;

    @Override
    public void readImpl() {
        _answer = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final Player requestor = activeChar.getRequest().getPartner();
        if (requestor == null) {
            return;
        }

        if (_answer == 0) {
            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_DIDN_T_RESPOND_TO_S1_S_INVITATION_JOINING_HAS_BEEN_CANCELLED);
            sm.addString(requestor.getName());
            activeChar.sendPacket(sm);
            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DID_NOT_RESPOND_INVITATION_TO_THE_CLAN_HAS_BEEN_CANCELLED);
            sm.addString(activeChar.getName());
            requestor.sendPacket(sm);
        } else {
            if (!((requestor.getRequest().getRequestPacket() instanceof RequestJoinPledge) || (requestor.getRequest().getRequestPacket() instanceof RequestClanAskJoinByName))) {
                return; // hax
            }

            final int pledgeType;
            if (requestor.getRequest().getRequestPacket() instanceof RequestJoinPledge)
            {
                pledgeType = ((RequestJoinPledge) requestor.getRequest().getRequestPacket()).getPledgeType();
            }
            else
            {
                pledgeType = ((RequestClanAskJoinByName) requestor.getRequest().getRequestPacket()).getPledgeType();
            }
            final Clan clan = requestor.getClan();
            // we must double check this cause during response time conditions can be changed, i.e. another player could join clan
            if (clan.checkClanJoinCondition(requestor, activeChar, pledgeType)) {
                if (activeChar.getClan() != null) {
                    return;
                }

                activeChar.sendPacket(new JoinPledge(requestor.getClanId()));

                activeChar.setPledgeType(pledgeType);
                if (pledgeType == Clan.SUBUNIT_ACADEMY) {
                    activeChar.setPowerGrade(9); // academy
                    activeChar.setLvlJoinedAcademy(activeChar.getLevel());
                } else {
                    activeChar.setPowerGrade(5); // new member starts at 5, not confirmed
                }

                clan.addClanMember(activeChar);
                activeChar.setClanPrivileges(activeChar.getClan().getRankPrivs(activeChar.getPowerGrade()));
                activeChar.sendPacket(SystemMessageId.ENTERED_THE_CLAN);

                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_JOINED_THE_CLAN);
                sm.addString(activeChar.getName());
                clan.broadcastToOnlineMembers(sm);

                if (clan.getCastleId() > 0) {
                    CastleManager.getInstance().getCastleByOwner(clan).giveResidentialSkills(activeChar);
                }
                activeChar.sendSkillList();

                clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListAdd(activeChar), activeChar);
                clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
                clan.broadcastToOnlineMembers(new ExPledgeCount(clan));

                // this activates the clan tab on the new member
                PledgeShowMemberListAll.sendAllTo(activeChar);
                activeChar.setClanJoinExpiryTime(0);
                activeChar.broadcastUserInfo();
            }
        }

        activeChar.getRequest().onRequestResponse();
    }
}
