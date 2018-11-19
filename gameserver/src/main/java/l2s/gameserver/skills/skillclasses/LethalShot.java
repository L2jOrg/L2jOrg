package l2s.gameserver.skills.skillclasses;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Formulas.AttackInfo;
import l2s.gameserver.templates.StatsSet;

public class LethalShot extends Skill
{
	public LethalShot(StatsSet set)
	{
		super(set);
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(target.isDead())
			return;

		if(getPower() <= 0)
			return;

		final Creature realTarget = reflected ? activeChar : target;
		final AttackInfo info = Formulas.calcPhysDam(activeChar, realTarget, this, false, false, isSSPossible(), false);
		if(info == null)
			return;

		realTarget.reduceCurrentHp(info.damage, activeChar, this, true, true, false, true, false, false, true, true, info.crit || info.blow, false, false);
		if(!info.miss || info.damage >= 1)
		{
			double lethalDmg = Formulas.calcLethalDamage(activeChar, realTarget, this);
			if(lethalDmg > 0)
				realTarget.reduceCurrentHp(lethalDmg, activeChar, this, true, true, false, false, false, false, false);
			else if(!reflected)
				realTarget.doCounterAttack(this, activeChar, false);
		}
	}
}