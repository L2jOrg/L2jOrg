package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.stats.Formulas;
import org.l2j.gameserver.stats.Formulas.AttackInfo;
import org.l2j.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class i_m_attack extends Effect
{
	public i_m_attack(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean onActionTime()
	{
		if(getEffected().isDead())
			return false;

		final Creature realTarget = isReflected() ? getEffector() : getEffected();
		final AttackInfo info = Formulas.calcMagicDam(getEffector(), realTarget, getSkill(), getValue(), getSkill().isSSPossible());

		realTarget.reduceCurrentHp(info.damage, getEffector(), getSkill(), true, true, false, true, false, false, getTemplate().isInstant(), getTemplate().isInstant(), info.crit, info.miss, info.shld);
		if(info.damage >= 1)
		{
			double lethalDmg = Formulas.calcLethalDamage(getEffector(), realTarget, getSkill());
			if(lethalDmg > 0)
				realTarget.reduceCurrentHp(lethalDmg, getEffector(), getSkill(), true, true, false, false, false, false, false);
		}
		return true;
	}
}