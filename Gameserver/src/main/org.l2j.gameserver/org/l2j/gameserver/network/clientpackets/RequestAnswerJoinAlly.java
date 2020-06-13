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

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;

public final class RequestAnswerJoinAlly extends ClientPacket {
    private int _response;

    @Override
    public void readImpl() {
        _response = readInt();
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

        if (_response == 0) {
            activeChar.sendPacket(SystemMessageId.NO_RESPONSE_YOUR_ENTRANCE_TO_THE_ALLIANCE_HAS_BEEN_CANCELLED);
            requestor.sendPacket(SystemMessageId.NO_RESPONSE_INVITATION_TO_JOIN_AN_ALLIANCE_HAS_BEEN_CANCELLED);
        } else {
            if (!(requestor.getRequest().getRequestPacket() instanceof RequestJoinAlly)) {
                return; // hax
            }

            final Clan clan = requestor.getClan();
            // we must double check this cause of hack
            if (clan.checkAllyJoinCondition(requestor, activeChar)) {
                // TODO: Need correct message id
                requestor.sendPacket(SystemMessageId.THAT_PERSON_HAS_BEEN_SUCCESSFULLY_ADDED_TO_YOUR_FRIEND_LIST);
                activeChar.sendPacket(SystemMessageId.YOU_HAVE_ACCEPTED_THE_ALLIANCE);

                activeChar.getClan().setAllyId(clan.getAllyId());
                activeChar.getClan().setAllyName(clan.getAllyName());
                activeChar.getClan().setAllyPenaltyExpiryTime(0, 0);
                activeChar.getClan().changeAllyCrest(clan.getAllyCrestId(), true);
                activeChar.getClan().updateClanInDB();
            }
        }

        activeChar.getRequest().onRequestResponse();
    }
}
