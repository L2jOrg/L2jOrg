package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class EffectMPDamPercent extends Effect
{
	public EffectMPDamPercent(final Abnormal abnormal, final Env env, final EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		if(getEffected().isDead())
			return;

		double newMp = (100. - getValue()) * getEffected().getMaxMp() / 100.;
		newMp = Math.min(getEffected().getCurrentMp(), Math.max(0, newMp));
		getEffected().setCurrentMp(newMp);
	}
}