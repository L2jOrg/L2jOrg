package org.l2j.scripts.handler.dailymissions;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.DailyMission;
import org.l2j.gameserver.templates.dailymissions.DailyMissionStatus;

/**
 * @author Bonux
**/
public class PledgeEnter extends ScriptDailyMissionHandler
{
	@Override
	public DailyMissionStatus getStatus(Player player, DailyMission mission)
	{
		if(mission.isCompleted())
			return DailyMissionStatus.COMPLETED;
		if(player.getClan() != null)
			return DailyMissionStatus.AVAILABLE;
		return DailyMissionStatus.NOT_AVAILABLE;
	}

	@Override
	public boolean isReusable()
	{
		return false;
	}
}
