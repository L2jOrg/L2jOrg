package l2s.gameserver.skills.skillclasses;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.templates.StatsSet;

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