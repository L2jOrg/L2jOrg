package l2s.gameserver.listener.actor.player;

import l2s.gameserver.listener.PlayerListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;

public interface OnPlayerSummonServitorListener extends PlayerListener
{
    public void onSummonServitor(Player player, Servitor servitor);
}