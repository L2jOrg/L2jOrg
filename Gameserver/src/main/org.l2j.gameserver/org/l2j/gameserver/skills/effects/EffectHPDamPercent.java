package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class EffectHPDamPercent extends Effect
{
	public EffectHPDamPercent(final Abnormal abnormal, final Env env, final EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		if(getEffected().isDead())
			return;

		double newHp = (100. - getValue()) * getEffected().getMaxHp() / 100.;
		newHp = Math.min(getEffected().getCurrentHp(), Math.max(0, newHp));
		getEffected().setCurrentHp(newHp, false);
	}
}