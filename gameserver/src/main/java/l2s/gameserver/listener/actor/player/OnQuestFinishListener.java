package l2s.gameserver.listener.actor.player;

import l2s.gameserver.listener.PlayerListener;
import l2s.gameserver.model.Player;

public interface OnQuestFinishListener extends PlayerListener
{
	public void onQuestFinish(Player player, int questId);
}