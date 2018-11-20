package org.l2j.gameserver.skills.skillclasses;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.stats.Formulas;
import org.l2j.gameserver.stats.Formulas.AttackInfo;
import org.l2j.gameserver.templates.StatsSet;

public class MDam extends Skill
{
	public MDam(StatsSet set)
	{
		super(set);
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(target.isDead())
			return;

		final Creature realTarget = reflected ? activeChar : target;
		final AttackInfo info = Formulas.calcMagicDam(activeChar, realTarget, this, isSSPossible());

		realTarget.reduceCurrentHp(info.damage, activeChar, this, true, true, false, true, false, false, true, true, info.crit, info.miss, info.shld);
		if(info.damage >= 1)
		{
			double lethalDmg = Formulas.calcLethalDamage(activeChar, realTarget, this);
			if(lethalDmg > 0)
				realTarget.reduceCurrentHp(lethalDmg, activeChar, this, true, true, false, false, false, false, false);
		}
	}
}