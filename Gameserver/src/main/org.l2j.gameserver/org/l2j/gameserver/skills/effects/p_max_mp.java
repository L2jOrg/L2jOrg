package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.stats.StatModifierType;
import org.l2j.gameserver.stats.Stats;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public final class p_max_mp extends p_abstract_stat_effect
{
	private final boolean _heal;

	public p_max_mp(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template, Stats.MAX_MP);
		_heal = template.getParam().getBool("heal", false);
	}

	@Override
	protected void afterApplyActions()
	{
		if(!_heal || getEffected().isHealBlocked())
			return;

		double power = getValue();
		if(getModifierType() == StatModifierType.PER)
			power = power / 100. * getEffected().getMaxMp();

		if(power > 0)
			getEffected().setCurrentMp(getEffected().getCurrentMp() + power, false);
	}
}
