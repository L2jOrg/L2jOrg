package l2s.gameserver.listener.actor.player;

import l2s.gameserver.listener.PlayerListener;
import l2s.gameserver.model.Player;

public interface OnFishingListener extends PlayerListener
{
    public void onFishing(Player player, boolean success);
}