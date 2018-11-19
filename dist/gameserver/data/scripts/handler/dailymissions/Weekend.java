package handler.dailymissions;

import java.util.Calendar;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.DailyMission;
import l2s.gameserver.templates.dailymissions.DailyMissionStatus;

/**
 * @author Bonux
**/
public class Weekend extends ScriptDailyMissionHandler
{
	@Override
	public DailyMissionStatus getStatus(Player player, DailyMission mission)
	{
		if(mission.isCompleted())
			return DailyMissionStatus.COMPLETED;

		Calendar currentCalendar = Calendar.getInstance();
		if(currentCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || currentCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			return DailyMissionStatus.AVAILABLE;

		return DailyMissionStatus.NOT_AVAILABLE;
	}

	@Override
	public int getProgress(Player player, DailyMission mission)
	{
		if(getStatus(player, mission) == DailyMissionStatus.NOT_AVAILABLE)
			return 0;
		return mission.getRequiredProgress();
	}
}
