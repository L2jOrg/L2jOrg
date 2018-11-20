package ai.locations.dragonvalley;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.ai.Fighter;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.utils.NpcUtils;

/**
 * @author Bonux
**/
public class CaveKeeper extends Fighter
{
	//Monster ID's
	private static final int CAVE_BANSHEE = 20412;

	private static final double SPAWN_CHANCE = 20.; // TODO: Проверить шанс.
	private static final int DESPAWN_TIME = 300000;

	public CaveKeeper(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);
		if(Rnd.chance(SPAWN_CHANCE))
		{
			final NpcInstance actor = getActor();
			ThreadPoolManager.getInstance().schedule(() ->
			{
				NpcInstance npc = NpcUtils.spawnSingle(CAVE_BANSHEE, actor.getLoc(), DESPAWN_TIME);
				npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 1000);
			}, 3000L);
		}
	}
}
