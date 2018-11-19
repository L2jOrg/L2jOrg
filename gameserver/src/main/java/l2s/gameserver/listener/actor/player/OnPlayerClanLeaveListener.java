package l2s.gameserver.listener.actor.player;

import l2s.gameserver.listener.PlayerListener;
import l2s.gameserver.model.Player;

public interface OnPlayerClanLeaveListener extends PlayerListener
{
    public void onClanLeave(Player player);
}