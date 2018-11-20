package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.Servitor;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public final class EffectDestroySummon extends Effect
{
	public EffectDestroySummon(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(!getEffected().isSummon())
			return false;
		return super.checkCondition();
	}

	@Override
	public boolean onActionTime()
	{
		((Servitor) getEffected()).unSummon(false);
		return true;
	}
}