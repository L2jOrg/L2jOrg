package org.l2j.scripts.handler.dailymissions;

import org.l2j.gameserver.listener.CharListener;
import org.l2j.gameserver.listener.actor.OnKillListener;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;

/**
 * @author Bonux
**/
public class DailyHunting extends ProgressDailyMissionHandler
{
	private class HandlerListeners implements OnKillListener
	{
		@Override
		public void onKill(Creature actor, Creature victim)
		{
			Player player = actor.getPlayer();
			if(player != null && victim.isMonster())
				progressMission(player, 1, true);
		}

		@Override
		public boolean ignorePetOrSummon()
		{
			return true;
		}
	}

	private final HandlerListeners _handlerListeners = new HandlerListeners();

	@Override
	public CharListener getListener()
	{
		return _handlerListeners;
	}
}
