package ai.others;

import ai.AbstractNpcAI;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Npc;

import static org.l2j.gameserver.util.GameUtils.isAttackable;

/**
 * See Through Silent Move AI.
 * @author Gigiikun
 */
public class SeeThroughSilentMove extends AbstractNpcAI
{
	//@formatter:off
	private static final int[] MONSTERS =
	{
		20142, 18002, 29009, 29010, 29011, 29012, 29013
	};
	//@formatter:on
	
	private SeeThroughSilentMove()
	{
		addSpawnId(MONSTERS);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		if (isAttackable(npc))
		{
			((Attackable) npc).setSeeThroughSilentMove(true);
		}
		return super.onSpawn(npc);
	}
	
	public static AbstractNpcAI provider()
	{
		return new SeeThroughSilentMove();
	}
}
