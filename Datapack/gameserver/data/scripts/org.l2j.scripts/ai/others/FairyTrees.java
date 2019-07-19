package ai.others;

import ai.AbstractNpcAI;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.util.MathUtil;

/**
 * Fairy Trees AI.
 * @author Charus
 */
public class FairyTrees extends AbstractNpcAI
{
	// NPC
	private static final int SOUL_GUARDIAN = 27189; // Soul of Tree Guardian
	
	private static final int[] MOBS =
	{
		27185, // Fairy Tree of Wind
		27186, // Fairy Tree of Star
		27187, // Fairy Tree of Twilight
		27188, // Fairy Tree of Abyss
	};
	
	// Skill
	private static SkillHolder VENOMOUS_POISON = new SkillHolder(4243, 1); // Venomous Poison
	
	// Misc
	private static final int MIN_DISTANCE = 1500;
	
	private FairyTrees()
	{
		addKillId(MOBS);
		addSpawnId(MOBS);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (MathUtil.isInsideRadius3D(npc, killer, MIN_DISTANCE))
		{
			for (int i = 0; i < 20; i++)
			{
				final Npc guardian = addSpawn(SOUL_GUARDIAN, npc, false, 30000);
				final Playable attacker = isSummon ? killer.getServitors().values().stream().findFirst().orElse(killer.getPet()) : killer;
				addAttackPlayerDesire(guardian, attacker);
				if (getRandomBoolean())
				{
					guardian.setTarget(attacker);
					guardian.doCast(VENOMOUS_POISON.getSkill());
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		npc.setRandomWalking(false);
		npc.setIsImmobilized(true);
		return super.onSpawn(npc);
	}
	
	public static AbstractNpcAI provider()
	{
		return new FairyTrees();
	}
}