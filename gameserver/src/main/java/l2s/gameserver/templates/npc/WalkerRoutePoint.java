package l2s.gameserver.templates.npc;

import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Location;

/**
 * @author Bonux
 **/
public class WalkerRoutePoint
{
	private final Location _loc;
	private final NpcString[] _phrases;
	private final int _socialActionId;
	private final int _delay;
	private final boolean _running;
	private final boolean _teleport;

	public WalkerRoutePoint(Location loc, NpcString[] phrases, int socialActionId, int delay, boolean running, boolean teleport)
	{
		_loc = loc;
		_phrases = phrases;
		_socialActionId = socialActionId;
		_delay = delay;
		_running = running;
		_teleport = teleport;
	}

	public Location getLocation()
	{
		return _loc;
	}

	public NpcString[] getPhrases()
	{
		return _phrases;
	}

	public int getSocialActionId()
	{
		return _socialActionId;
	}

	public int getDelay()
	{
		return _delay;
	}

	public boolean isRunning()
	{
		return _running;
	}

	public boolean isTeleport()
	{
		return _teleport;
	}
}