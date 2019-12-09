package ai.others;

import ai.AbstractNpcAI;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.MinionHolder;
import org.l2j.gameserver.network.NpcStringId;

import static org.l2j.gameserver.util.GameUtils.isMonster;

/**
 * Timak Orc Troop Leader AI.
 * @author Mobius
 */
public final class TimakOrcTroopLeader extends AbstractNpcAI
{
	private static final int TIMAK_ORC_TROOP_LEADER = 20767;
	private static final NpcStringId[] ON_ATTACK_MSG =
	{
		NpcStringId.COME_OUT_YOU_CHILDREN_OF_DARKNESS,
		NpcStringId.SHOW_YOURSELVES,
		NpcStringId.DESTROY_THE_ENEMY_MY_BROTHERS,
		NpcStringId.FORCES_OF_DARKNESS_FOLLOW_ME
	};
	
	private TimakOrcTroopLeader()
	{
		addAttackId(TIMAK_ORC_TROOP_LEADER);
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		if (isMonster(npc))
		{
			final Monster monster = (Monster) npc;
			if (!monster.isTeleporting())
			{
				if (getRandom(1, 100) <= npc.getParameters().getInt("SummonPrivateRate", 0))
				{
					for (MinionHolder is : npc.getParameters().getMinionList("Privates"))
					{
						addMinion((Monster) npc, is.getId());
					}
					npc.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(ON_ATTACK_MSG));
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	public static AbstractNpcAI provider()
	{
		return new TimakOrcTroopLeader();
	}
}