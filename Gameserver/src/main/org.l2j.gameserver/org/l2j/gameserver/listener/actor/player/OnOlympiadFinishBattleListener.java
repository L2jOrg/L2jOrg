package org.l2j.gameserver.listener.actor.player;

import org.l2j.gameserver.listener.PlayerListener;
import org.l2j.gameserver.model.Player;

public interface OnOlympiadFinishBattleListener extends PlayerListener
{
	public void onOlympiadFinishBattle(Player player, boolean winner);
}