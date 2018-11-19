package handler.dailymissions;

import l2s.gameserver.listener.CharListener;
import l2s.gameserver.listener.actor.player.OnQuestFinishListener;
import l2s.gameserver.model.Player;

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
