package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class EffectConsumeSoulsOverTime extends Effect
{
	public EffectConsumeSoulsOverTime(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean onActionTime()
	{
		if(getEffected().isDead())
			return false;

		if(getEffected().getConsumedSouls() < 0)
			return false;

		int damage = (int) getValue();

		if(getEffected().getConsumedSouls() < damage)
			getEffected().setConsumedSouls(0, null);
		else
			getEffected().setConsumedSouls(getEffected().getConsumedSouls() - damage, null);

		return true;
	}
}