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
import org.l2j.gameserver.enums.PartyDistributionType;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.PartyRequest;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.AskJoinParty;
import org.l2j.gameserver.world.World;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

public final class RequestJoinParty extends ClientPacket {
    private String name;
    private int partyDistributionTypeId;

    @Override
    public void readImpl() {
        name = readString();
        partyDistributionTypeId = readInt();
    }

    @Override
    public void runImpl() {
        var requestor = client.getPlayer();
        var target = World.getInstance().findPlayer(name);

        if (isNull(target)) {
            requestor.sendPacket(SystemMessageId.YOU_MUST_FIRST_SELECT_A_USER_TO_INVITE_TO_YOUR_PARTY);
            return;
        }

        if (isNull(target.getClient()) || target.getClient().isDetached()) {
            requestor.sendPacket(getSystemMessage(SystemMessageId.S1_CURRENTLY_OFFLINE).addString(name));
            return;
        }

        if (requestor.isPartyBanned()) {
            requestor.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_PARTICIPATING_IN_A_PARTY_IS_NOT_ALLOWED);
            return;
        }

        if (target.isPartyBanned()) {
            requestor.sendPacket(getSystemMessage(SystemMessageId.C1_HAS_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_CANNOT_JOIN_A_PARTY).addString(name));
            return;
        }

        if (requestor.isOnEvent()) // custom event message
        {
            requestor.sendMessage("You cannot invite to a party while participating in an event.");
            return;
        }

        if (target.isInParty()) {
            requestor.sendPacket(getSystemMessage(SystemMessageId.C1_IS_A_MEMBER_OF_ANOTHER_PARTY_AND_CANNOT_BE_INVITED).addString(name));
            return;
        }

        if (BlockList.isBlocked(target, requestor)) {
            requestor.sendPacket(getSystemMessage(SystemMessageId.C1_HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST).addString(name));
            return;
        }

        if (target == requestor) {
            requestor.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
            return;
        }

        if (target.isJailed() || requestor.isJailed()) {
            requestor.sendMessage("You cannot invite a player while is in Jail.");
            return;
        }

        if (target.isInOlympiadMode() || requestor.isInOlympiadMode()) {
            if ((target.isInOlympiadMode() != requestor.isInOlympiadMode()) || (target.getOlympiadGameId() != requestor.getOlympiadGameId()) || (target.getOlympiadSide() != requestor.getOlympiadSide())) {
                requestor.sendPacket(SystemMessageId.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS);
                return;
            }
        }

        if(target.hasRequest(PartyRequest.class)) {
            requestor.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
            return;
        }

        requestor.sendPacket(getSystemMessage(SystemMessageId.C1_HAS_BEEN_INVITED_TO_THE_PARTY).addString(target.getName()));

        if (!requestor.isInParty()) {
            createNewParty(target, requestor);
        } else {
            addTargetToParty(target, requestor);
        }
    }
    
    private void addTargetToParty(Player target, Player requestor) {
        final Party party = requestor.getParty();

        // summary of ppl already in party and ppl that get invitation
        if (!party.isLeader(requestor)) {
            requestor.sendPacket(SystemMessageId.ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS);
        } else if (party.getMemberCount() >= Config.ALT_PARTY_MAX_MEMBERS) {
            requestor.sendPacket(SystemMessageId.THE_PARTY_IS_FULL);
        } else if (party.getPendingInvitation() && !party.isInvitationRequestExpired()) {
            requestor.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
        } else{
            sendPartyRequest(target, requestor, party.getDistributionType(), party);
        }
    }

    private void createNewParty(Player target, Player requestor) {
        final PartyDistributionType partyDistributionType = PartyDistributionType.findById(partyDistributionTypeId);
        if (partyDistributionType == null) {
            return;
        }
        var party = new Party(requestor, partyDistributionType);
        sendPartyRequest(target, requestor, partyDistributionType, party);
    }

    private void sendPartyRequest(Player target, Player requestor, PartyDistributionType partyDistributionType, Party party) {
        var request = new PartyRequest(requestor, target, party);
        request.scheduleTimeout(30 * 1000);
        requestor.addRequest(request);
        target.addRequest(request);
        target.sendPacket(new AskJoinParty(requestor.getName(), partyDistributionType));
        party.setPendingInvitation(true);
    }
}
