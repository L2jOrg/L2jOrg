package ai.locations.loa;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.ai.Mystic;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.utils.NpcUtils;

/**
 * @author Bonux
**/
public class Pytan extends Mystic
{
	//Monster ID's
	private static final int KNORIKS = 20405;

	private static final double SPAWN_CHANCE = 5.; // TODO: Проверить шанс.
	private static final int DESPAWN_TIME = 300000;

	public Pytan(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);
		// TODO: Нужна ли тут задержка?
		if(Rnd.chance(SPAWN_CHANCE))
		{
			NpcInstance actor = getActor();

			NpcInstance npc = NpcUtils.spawnSingle(KNORIKS, actor.getLoc(), DESPAWN_TIME);
			npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 1000);

			//actor.doDecay(); TODO: Нужно ли? А то со спойлом будут проблемы.
		}
	}
}
