package l2s.gameserver.skills.skillclasses;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.templates.StatsSet;

public class CPDam extends Skill
{
	public CPDam(StatsSet set)
	{
		super(set);
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(target.isDead())
			return;

		target.doCounterAttack(this, activeChar, false);

		final Creature realTarget = reflected ? activeChar : target;
		if(realTarget.isCurrentCpZero())
			return;

		final double damage = Math.max(1, getPower() * realTarget.getCurrentCp());
		realTarget.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, false, true);
	}
}