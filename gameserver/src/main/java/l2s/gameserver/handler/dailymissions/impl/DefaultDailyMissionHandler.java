package l2s.gameserver.handler.dailymissions.impl;

import l2s.commons.time.cron.SchedulingPattern;
import l2s.gameserver.handler.dailymissions.IDailyMissionHandler;
import l2s.gameserver.listener.CharListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.DailyMission;
import l2s.gameserver.templates.dailymissions.DailyMissionStatus;

/**
 * @author Bonux
 **/
public class DefaultDailyMissionHandler implements IDailyMissionHandler
{
	private static final SchedulingPattern REUSE_PATTERN = new SchedulingPattern("30 6 * * *");

	public CharListener getListener()
	{
		return null;
	}

	@Override
	public DailyMissionStatus getStatus(Player player, DailyMission mission)
	{
		return DailyMissionStatus.NOT_AVAILABLE;
	}

	@Override
	public int getProgress(Player player, DailyMission mission)
	{
		return mission.getValue();
	}

	@Override
	public boolean isReusable()
	{
		return true;
	}

	@Override
	public SchedulingPattern getReusePattern()
	{
		return REUSE_PATTERN;
	}
}