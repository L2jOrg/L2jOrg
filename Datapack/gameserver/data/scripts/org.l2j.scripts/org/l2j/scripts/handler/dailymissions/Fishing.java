package org.l2j.scripts.handler.dailymissions;

import org.l2j.gameserver.listener.CharListener;
import org.l2j.gameserver.listener.actor.player.OnFishingListener;
import org.l2j.gameserver.model.Player;

/**
 * @author Bonux
**/
public class Fishing extends ProgressDailyMissionHandler
{
	private class HandlerListeners implements OnFishingListener
	{
		@Override
		public void onFishing(Player player, boolean success)
		{
			if(!success) // TODO: Проверить на оффе.
				return;

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
