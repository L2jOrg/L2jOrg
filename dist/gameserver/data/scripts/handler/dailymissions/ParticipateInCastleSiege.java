package handler.dailymissions;

import l2s.commons.time.cron.SchedulingPattern;
import l2s.gameserver.listener.CharListener;
import l2s.gameserver.listener.actor.player.OnParticipateInCastleSiegeListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.impl.CastleSiegeEvent;

/**
 * @author Bonux
**/
public class ParticipateInCastleSiege extends ProgressDailyMissionHandler
{
	private static final SchedulingPattern REUSE_PATTERN = new SchedulingPattern("30 6 * * 1");

	private class HandlerListeners implements OnParticipateInCastleSiegeListener
	{
		@Override
		public void onParticipateInCastleSiege(Player player, CastleSiegeEvent siegeEvent)
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

	@Override
	public SchedulingPattern getReusePattern()
	{
		return REUSE_PATTERN;
	}
}
