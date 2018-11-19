package handler.dailymissions;

import l2s.gameserver.listener.CharListener;
import l2s.gameserver.listener.actor.OnKillListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;

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
