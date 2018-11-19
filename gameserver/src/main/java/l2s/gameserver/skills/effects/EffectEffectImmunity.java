package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public final class EffectEffectImmunity extends Effect
{
	private final boolean _withException;

	public EffectEffectImmunity(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
		_withException = template.getParam().getBool("with_exception", false);
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getEffectImmunity().start(this);
		if(_withException)
		{
			if(getEffected() == getEffector())
				getEffected().setEffectImmunityException(getEffector().getCastingTarget());
			else
				getEffected().setEffectImmunityException(getEffector());
		}
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getEffectImmunity().stop(this);
		getEffected().setEffectImmunityException(null);
	}
}