package handler.dailymissions;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.DailyMission;

/**
 * @author Bonux
**/
public class LevelUp extends ProgressDailyMissionHandler
{
	@Override
	public int getProgress(Player player, DailyMission mission)
	{
		return player.getBaseSubClass().getLevel();
	}

	@Override
	public boolean isReusable()
	{
		return false;
	}
}
