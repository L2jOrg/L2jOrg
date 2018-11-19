package l2s.gameserver.model.entity.events.objects;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

import l2s.gameserver.model.Player;

/**
 LIO
 25.02.2016
 */
public class PvPEventPlayerObject
{
	private final Player player;
	private final int team;
	private AtomicInteger countDie;
	private AtomicInteger points;
	private boolean teleport;
	private ScheduledFuture<?> scheduled = null;

	public PvPEventPlayerObject(Player player, int team)
	{
		this.player = player;
		this.team = team;
		countDie = new AtomicInteger(0);
		points = new AtomicInteger(0);
	}

	public Player getPlayer()
	{
		return player;
	}

	public int getTeam()
	{
		return team;
	}

	public void addCountDie()
	{
		countDie.getAndAdd(1);
	}

	public int getCountDie()
	{
		return countDie.get();
	}

	public void addPoint()
	{
		points.getAndAdd(1);
	}

	public int getPoints()
	{
		return points.get();
	}

	public boolean isTeleport()
	{
		return teleport;
	}

	public void setTeleport(boolean teleport)
	{
		this.teleport = teleport;
	}

	public ScheduledFuture<?> getScheduled()
	{
		return scheduled;
	}

	public void setScheduled(ScheduledFuture<?> scheduled)
	{
		stopScheduled();
		this.scheduled = scheduled;
	}

	public void stopScheduled()
	{
		if(scheduled != null)
		{
			scheduled.cancel(false);
			scheduled = null;
		}
	}
}