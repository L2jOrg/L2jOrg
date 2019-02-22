package org.l2j.scripts.handler.dailymissions;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.DailyMission;
import org.l2j.gameserver.templates.dailymissions.DailyMissionStatus;
import org.l2j.gameserver.templates.dailymissions.DailyMissionTemplate;

import java.util.Collection;

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
