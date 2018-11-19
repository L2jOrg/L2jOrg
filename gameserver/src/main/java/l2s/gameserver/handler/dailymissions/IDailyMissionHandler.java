package l2s.gameserver.handler.dailymissions;

import l2s.commons.time.cron.SchedulingPattern;
import l2s.gameserver.listener.CharListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.DailyMission;
import l2s.gameserver.templates.dailymissions.DailyMissionStatus;

/**
 * @author Bonux
 **/
public interface IDailyMissionHandler
{
	public CharListener getListener();

	public DailyMissionStatus getStatus(Player player, DailyMission mission);

	public int getProgress(Player owner, DailyMission mission);

	public boolean isReusable();

	public SchedulingPattern getReusePattern();
}