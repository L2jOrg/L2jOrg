package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
 **/
public final class EffectDamageBlock extends Effect
{
	private final boolean _withException;

	public EffectDamageBlock(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
		_withException = template.getParam().getBool("with_exception", false);
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getDamageBlocked().start(this);
		if(_withException)
		{
			if(getEffected() == getEffector())
				getEffected().setDamageBlockedException(getEffector().getCastingTarget());
			else
				getEffected().setDamageBlockedException(getEffector());
		}
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getDamageBlocked().stop(this);
		getEffected().setDamageBlockedException(null);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}