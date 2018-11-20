package org.l2j.gameserver.listener.actor.player;

import org.l2j.gameserver.listener.PlayerListener;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.components.ChatType;

public interface OnPlayerChatMessageReceive extends PlayerListener
{
    public void onChatMessageReceive(Player player, ChatType type, String charName, String text);
}