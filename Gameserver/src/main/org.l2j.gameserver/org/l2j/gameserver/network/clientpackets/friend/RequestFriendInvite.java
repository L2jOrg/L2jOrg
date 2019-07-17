package org.l2j.gameserver.network.clientpackets.friend;

import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.ceremonyofchaos.CeremonyOfChaosEvent;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.friend.FriendAddRequest;

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
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Player friend = World.getInstance().findPlayer(_name);

        // Target is not found in the game.
        if ((friend == null) || !friend.isOnline() || friend.isInvisible()) {
            activeChar.sendPacket(SystemMessageId.THE_USER_WHO_REQUESTED_TO_BECOME_FRIENDS_IS_NOT_FOUND_IN_THE_GAME);
            return;
        }
        // You cannot add yourself to your own friend list.
        if (friend == activeChar) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST);
            return;
        }
        // Target is in olympiad.
        if (activeChar.isInOlympiadMode() || friend.isInOlympiadMode()) {
            activeChar.sendPacket(SystemMessageId.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS);
            return;
        }

        // Cannot request friendship in Ceremony of Chaos event.
        if (activeChar.isOnEvent(CeremonyOfChaosEvent.class)) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_INVITE_A_FRIEND_OR_PARTY_WHILE_PARTICIPATING_IN_THE_CEREMONY_OF_CHAOS);
            return;
        }

        // Target blocked active player.
        if (BlockList.isBlocked(friend, activeChar)) {
            activeChar.sendMessage("You are in target's block list.");
            return;
        }
        SystemMessage sm;
        // Target is blocked.
        if (BlockList.isBlocked(activeChar, friend)) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_BLOCKED_C1);
            sm.addString(friend.getName());
            activeChar.sendPacket(sm);
            return;
        }

        // Target already in friend list.
        if (activeChar.getFriendList().contains(friend.getObjectId())) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.THIS_PLAYER_IS_ALREADY_REGISTERED_ON_YOUR_FRIENDS_LIST);
            sm.addString(_name);
            activeChar.sendPacket(sm);
            return;
        }
        // Target is busy.
        if (friend.isProcessingRequest()) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER);
            sm.addString(_name);
            activeChar.sendPacket(sm);
            return;
        }
        // Friend request sent.
        activeChar.onTransactionRequest(friend);
        friend.sendPacket(new FriendAddRequest(activeChar.getName()));
        sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_VE_REQUESTED_C1_TO_BE_ON_YOUR_FRIENDS_LIST);
        sm.addString(_name);
        activeChar.sendPacket(sm);
    }
}
