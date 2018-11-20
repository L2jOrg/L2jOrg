package ai;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.ai.NpcAI;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.s2c.MagicSkillUse;
import org.l2j.gameserver.utils.Location;

/**
 * @author Bonux
**/
public class Toma extends NpcAI
{
	private static final long TELEPORT_DELAY = 1800000L; // 30 минут
	private Location[] TELEPORT_POINTS = {
		new Location(151680, -174891, 63729, 41400),
		new Location(154153, -220105, 62134, 0),
		new Location(178834, -184336, 65184, 0)
	};

	private long _lastTeleport = System.currentTimeMillis();

	public Toma(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		if((System.currentTimeMillis() - _lastTeleport) < TELEPORT_DELAY)
			return false;

		final NpcInstance actor = getActor();
		final Location loc = Rnd.get(TELEPORT_POINTS);

		if(actor.isInRange(loc, 50))
			return false;

		_lastTeleport = System.currentTimeMillis();

		actor.broadcastPacket(new MagicSkillUse(actor, 4671, 1, 1000, 0L));
		ThreadPoolManager.getInstance().schedule(() -> actor.teleToLocation(loc), 1000L);
		return true;
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
}
