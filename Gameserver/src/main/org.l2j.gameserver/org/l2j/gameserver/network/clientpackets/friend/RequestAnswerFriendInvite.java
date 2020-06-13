/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.network.clientpackets.friend;

import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.friend.FriendAddRequestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

public final class RequestAnswerFriendInvite extends ClientPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestAnswerFriendInvite.class);

    private int _response;

    @Override
    public void readImpl() {
        readByte();
        _response = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        final Player requestor = player.getActiveRequester();
        if (requestor == null) {
            return;
        }

        if (player == requestor) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST);
            return;
        }

        if (player.getFriendList().contains(requestor.getObjectId()) //
                || requestor.getFriendList().contains(player.getObjectId())) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ALREADY_ON_YOUR_FRIEND_LIST);
            sm.addString(player.getName());
            requestor.sendPacket(sm);
            return;
        }

        if (_response == 1) {
            getDAO(PlayerDAO.class).saveFriendship(requestor.getObjectId(), player.getObjectId());
            SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.THAT_PERSON_HAS_BEEN_SUCCESSFULLY_ADDED_TO_YOUR_FRIEND_LIST);
            requestor.sendPacket(msg);

            // Player added to your friend list
            msg = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_ADDED_TO_YOUR_FRIENDS_LIST);
            msg.addString(player.getName());
            requestor.sendPacket(msg);
            requestor.getFriendList().add(player.getObjectId());

            // has joined as friend.
            msg = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_ADDED_TO_YOUR_FRIENDS_LIST);
            msg.addString(requestor.getName());
            player.sendPacket(msg);
            player.getFriendList().add(requestor.getObjectId());

            // Send notifications for both player in order to show them online
            player.sendPacket(new FriendAddRequestResult(requestor, 1));
            requestor.sendPacket(new FriendAddRequestResult(player, 1));
        } else {
            final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_ADD_A_FRIEND_TO_YOUR_FRIENDS_LIST);
            requestor.sendPacket(msg);
        }

        player.setActiveRequester(null);
        requestor.onTransactionResponse();
    }
}
