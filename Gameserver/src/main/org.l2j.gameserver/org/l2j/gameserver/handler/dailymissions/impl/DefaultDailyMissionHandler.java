package org.l2j.gameserver.handler.dailymissions.impl;

import org.l2j.commons.time.cron.SchedulingPattern;
import org.l2j.gameserver.handler.dailymissions.IDailyMissionHandler;
import org.l2j.gameserver.listener.CharListener;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.DailyMission;
import org.l2j.gameserver.templates.dailymissions.DailyMissionStatus;

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