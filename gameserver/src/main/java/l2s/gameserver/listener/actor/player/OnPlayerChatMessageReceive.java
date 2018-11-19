package l2s.gameserver.listener.actor.player;

import l2s.gameserver.listener.PlayerListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.ChatType;

public interface OnPlayerChatMessageReceive extends PlayerListener
{
    public void onChatMessageReceive(Player player, ChatType type, String charName, String text);
}