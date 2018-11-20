package org.l2j.gameserver.handler.dailymissions;

import org.l2j.commons.time.cron.SchedulingPattern;
import org.l2j.gameserver.listener.CharListener;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.DailyMission;
import org.l2j.gameserver.templates.dailymissions.DailyMissionStatus;

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