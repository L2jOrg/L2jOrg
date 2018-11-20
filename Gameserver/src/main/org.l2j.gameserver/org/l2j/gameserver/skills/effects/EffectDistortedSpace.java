package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public final class EffectDistortedSpace extends Effect
{
	public EffectDistortedSpace(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getDistortedSpace().start(this);
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getDistortedSpace().stop(this);
	}
}