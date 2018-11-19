package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Formulas.AttackInfo;
import l2s.gameserver.templates.skill.EffectTemplate;

public class i_p_hit extends i_abstract_effect
{
	private final boolean _canCrit;

	public i_p_hit(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
		_canCrit = template.getParam().getBool("can_critical", false);
	}

	@Override
	public boolean checkCondition()
	{
		if(getEffected().isDead())
			return false;
		return super.checkCondition();
	}

	@Override
	public void instantUse()
	{
		boolean dual = false;

		switch(getEffector().getBaseStats().getAttackType())
		{
			case DUAL:
			case DUALFIST:
			case DUALDAGGER:
			case DUALBLUNT:
				dual = true;
				break;
		}

		int damage = 0;
		boolean shld = false;
		boolean crit = false;

		AttackInfo info = Formulas.calcPhysDam(getEffector(), getEffected(), null, getValue(), 0, dual, false, getEffector().getChargedSoulshotPower() > 0, false, _canCrit);
		if(info != null)
		{
			damage = (int) info.damage;
			shld = info.shld;
			crit = info.crit;
		}

		getEffected().reduceCurrentHp(damage, getEffector(), null, true, true, false, true, false, false, true, true, crit, false, shld);

		if(dual)
		{
			damage = 0;
			shld = false;
			crit = false;

			info = Formulas.calcPhysDam(getEffector(), getEffected(), null, getValue(), 0, dual, false, getEffector().getChargedSoulshotPower() > 0, false, _canCrit);
			if(info != null)
			{
				damage = (int) info.damage;
				shld = info.shld;
				crit = info.crit;
			}

			getEffected().reduceCurrentHp(damage, getEffector(), null, true, true, false, true, false, false, true, true, crit, false, shld);
		}
	}
}