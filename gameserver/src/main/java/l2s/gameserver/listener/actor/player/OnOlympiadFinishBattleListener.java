package l2s.gameserver.listener.actor.player;

import l2s.gameserver.listener.PlayerListener;
import l2s.gameserver.model.Player;

public interface OnOlympiadFinishBattleListener extends PlayerListener
{
	public void onOlympiadFinishBattle(Player player, boolean winner);
}