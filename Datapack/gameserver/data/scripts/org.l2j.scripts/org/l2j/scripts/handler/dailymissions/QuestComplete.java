package org.l2j.scripts.handler.dailymissions;

import org.l2j.gameserver.listener.CharListener;
import org.l2j.gameserver.listener.actor.player.OnQuestFinishListener;
import org.l2j.gameserver.model.Player;

/**
 * @author Bonux
**/
public class QuestComplete extends ProgressDailyMissionHandler
{
	private class HandlerListeners implements OnQuestFinishListener
	{
		@Override
		public void onQuestFinish(Player player, int questId)
		{
			progressMission(player, 1, true);
		}
	}

	private final HandlerListeners _handlerListeners = new HandlerListeners();

	@Override
	public CharListener getListener()
	{
		return _handlerListeners;
	}
}
