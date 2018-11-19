package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Formulas.AttackInfo;
import l2s.gameserver.templates.skill.EffectTemplate;

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