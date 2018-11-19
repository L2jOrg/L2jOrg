package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

public class QueenAntLarvaInstance extends MonsterInstance
{
	private static final long serialVersionUID = 1L;

	public QueenAntLarvaInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflectAndAbsorb, boolean transferDamage, boolean isDot, boolean sendReceiveMessage, boolean sendGiveMessage, boolean crit, boolean miss, boolean shld)
	{
		damage = getCurrentHp() - damage > 1 ? damage : getCurrentHp() - 1;
		super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflectAndAbsorb, transferDamage, isDot, sendReceiveMessage, sendGiveMessage, crit, miss, shld);
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}
	
	@Override
	public boolean isImmobilized()
	{
		return true;
	}
}