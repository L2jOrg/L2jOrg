package org.l2j.gameserver.skills.skillclasses;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.templates.StatsSet;

public class DebuffRenewal extends Skill
{
	public DebuffRenewal(StatsSet set)
	{
		super(set);
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		renewEffects(target);
		target.updateAbnormalIcons();
	}

	private void renewEffects(Creature target)
	{
		TIntSet skillsToRefresh = new TIntHashSet();

		for(Abnormal effect : target.getAbnormalList())
		{
			if(effect.isOffensive() && effect.getSkill().isRenewal())
				skillsToRefresh.add(effect.getSkill().getId());
		}

		for(Abnormal effect : target.getAbnormalList())
		{
			if(skillsToRefresh.contains(effect.getSkill().getId()) && effect.getSkill().isRenewal())
				effect.restart();
		}
	}
}