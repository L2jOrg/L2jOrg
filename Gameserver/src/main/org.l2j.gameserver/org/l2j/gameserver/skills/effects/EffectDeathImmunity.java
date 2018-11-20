package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

/**
 * Target is immune to death.
 *
 * @author Yorie
 */
public final class EffectDeathImmunity extends Effect
{
	public EffectDeathImmunity(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getDeathImmunity().start(this);
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getDeathImmunity().stop(this);
	}
}