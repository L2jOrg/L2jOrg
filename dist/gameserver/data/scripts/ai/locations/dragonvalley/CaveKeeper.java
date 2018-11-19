package ai.locations.dragonvalley;

import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.NpcUtils;

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
