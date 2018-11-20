package handler.dailymissions;

import org.l2j.commons.time.cron.SchedulingPattern;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.DailyMission;
import org.l2j.gameserver.templates.dailymissions.DailyMissionStatus;

/**
 * @author Bonux
**/
public class Monthly extends ScriptDailyMissionHandler
{
	private static final SchedulingPattern REUSE_PATTERN = new SchedulingPattern("30 6 1 * *");

	@Override
	public DailyMissionStatus getStatus(Player player, DailyMission mission)
	{
		if(mission.isCompleted())
			return DailyMissionStatus.COMPLETED;
		return DailyMissionStatus.AVAILABLE;
	}

	@Override
	public SchedulingPattern getReusePattern()
	{
		return REUSE_PATTERN;
	}

	@Override
	public int getProgress(Player player, DailyMission mission)
	{
		if(getStatus(player, mission) == DailyMissionStatus.NOT_AVAILABLE)
			return 0;
		return mission.getRequiredProgress();
	}
}
