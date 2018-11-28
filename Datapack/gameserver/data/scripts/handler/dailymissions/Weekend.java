package handler.dailymissions;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.DailyMission;
import org.l2j.gameserver.templates.dailymissions.DailyMissionStatus;

import java.util.Calendar;

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
