package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public final class EffectPetrification extends Effect
{
	public EffectPetrification(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(getEffected().isParalyzeImmune())
			return false;
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getParalyzed().start(this);
		getEffected().getFlags().getDebuffImmunity().start(this);
		getEffected().getFlags().getBuffImmunity().start(this);
		if(getEffected() != getEffector())
		{
			getEffected().abortAttack(true, true);
			getEffected().abortCast(true, true);
		}
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getParalyzed().stop(this);
		getEffected().getFlags().getDebuffImmunity().stop(this);
		getEffected().getFlags().getBuffImmunity().stop(this);
	}
}