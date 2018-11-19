package ai;

import java.util.List;

import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.EarthQuakePacket;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;

/**
 * AI каменной статуи Байума.<br>
 * Раз в 15 минут устраивает замлятрясение а ТОИ.
 *
 * @author SYS
 */
public class BaiumNpc extends DefaultAI
{
	private long _wait_timeout = 0;
	private static final int BAIUM_EARTHQUAKE_TIMEOUT = 1000 * 60 * 15; // 15 мин

	public BaiumNpc(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		// Пора устроить землятрясение
		if(_wait_timeout < System.currentTimeMillis())
		{
			_wait_timeout = System.currentTimeMillis() + BAIUM_EARTHQUAKE_TIMEOUT;
			L2GameServerPacket eq = new EarthQuakePacket(actor.getLoc(), 40, 10);
			List<Creature> chars = actor.getAroundCharacters(5000, 10000);
			for(Creature character : chars)
				if(character.isPlayer())
					character.sendPacket(eq);
		}
		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}