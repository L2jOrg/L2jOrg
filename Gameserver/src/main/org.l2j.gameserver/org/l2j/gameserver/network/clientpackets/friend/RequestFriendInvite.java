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
package org.l2j.gameserver.network.clientpackets.friend;

import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.friend.FriendAddRequest;
import org.l2j.gameserver.world.World;

public final class RequestFriendInvite extends ClientPacket {
    private String _name;

    @Override
    public void readImpl() {
        _name = readString();
    }

    private void scheduleDeny(Player player) {
        if (player != null) {
            player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_ADD_A_FRIEND_TO_YOUR_FRIENDS_LIST));
            player.onTransactionResponse();
        }
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        final Player friend = World.getInstance().findPlayer(_name);

        // Target is not found in the game.
        if ((friend == null) || !friend.isOnline() || friend.isInvisible()) {
            player.sendPacket(SystemMessageId.THE_USER_WHO_REQUESTED_TO_BECOME_FRIENDS_IS_NOT_FOUND_IN_THE_GAME);
            return;
        }
        // You cannot add yourself to your own friend list.
        if (friend == player) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST);
            return;
        }
        // Target is in olympiad.
        if (player.isInOlympiadMode() || friend.isInOlympiadMode()) {
            player.sendPacket(SystemMessageId.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS);
            return;
        }

        // Target blocked active player.
        if (BlockList.isBlocked(friend, player)) {
            player.sendMessage("You are in target's block list.");
            return;
        }
        SystemMessage sm;
        // Target is blocked.
        if (BlockList.isBlocked(player, friend)) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_BLOCKED_C1);
            sm.addString(friend.getName());
            player.sendPacket(sm);
            return;
        }

        // Target already in friend list.
        if (player.getFriendList().contains(friend.getObjectId())) {
            player.sendPacket(SystemMessageId.THIS_PLAYER_IS_ALREADY_REGISTERED_ON_YOUR_FRIENDS_LIST);
            return;
        }
        // Target is busy.
        if (friend.isProcessingRequest()) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER);
            sm.addString(_name);
            player.sendPacket(sm);
            return;
        }
        // Friend request sent.
        player.onTransactionRequest(friend);
        friend.sendPacket(new FriendAddRequest(player.getName()));
        sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_VE_REQUESTED_C1_TO_BE_ON_YOUR_FRIENDS_LIST);
        sm.addString(_name);
        player.sendPacket(sm);
    }
}
