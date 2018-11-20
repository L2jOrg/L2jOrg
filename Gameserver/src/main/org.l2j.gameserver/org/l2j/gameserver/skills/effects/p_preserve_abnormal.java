package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public final class p_preserve_abnormal extends Effect
{
	public p_preserve_abnormal(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		getEffected().setPreserveAbnormal(true);
	}

	@Override
	public void onExit()
	{
		getEffected().setPreserveAbnormal(false);
	}
}