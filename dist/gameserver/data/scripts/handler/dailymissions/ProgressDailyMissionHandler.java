package handler.dailymissions;

import java.util.Collection;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.DailyMission;
import l2s.gameserver.templates.dailymissions.DailyMissionStatus;
import l2s.gameserver.templates.dailymissions.DailyMissionTemplate;

/**
 * @author Bonux
**/
public abstract class ProgressDailyMissionHandler extends ScriptDailyMissionHandler
{
	@Override
	public DailyMissionStatus getStatus(Player player, DailyMission mission)
	{
		if(mission.isCompleted())
			return DailyMissionStatus.COMPLETED;
		if(mission.getCurrentProgress() >= mission.getRequiredProgress())
			return DailyMissionStatus.AVAILABLE;
		return DailyMissionStatus.NOT_AVAILABLE;
	}

	protected void progressMission(Player player, int value, boolean increase)
	{
		if(!Config.EX_USE_TO_DO_LIST)
			return;

		Collection<DailyMissionTemplate> missionTemplates = player.getDailyMissionList().getAvailableMissions();
		for(DailyMissionTemplate missionTemplate : missionTemplates)
		{
			if(missionTemplate.getHandler() != this)
				continue;

			DailyMission mission = player.getDailyMissionList().get(missionTemplate);
			if(mission.isCompleted())
				continue;

			if(increase)
			{
				mission.setValue(mission.getValue() + value);
			}
			else
			{
				if(mission.getValue() == value)
					continue;

				mission.setValue(value);
			}
		}
	}
}
