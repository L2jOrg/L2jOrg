package org.l2j.scripts.handler.dailymissions;

import org.l2j.commons.time.cron.SchedulingPattern;
import org.l2j.gameserver.listener.CharListener;
import org.l2j.gameserver.listener.actor.player.OnParticipateInCastleSiegeListener;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.events.impl.CastleSiegeEvent;

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
