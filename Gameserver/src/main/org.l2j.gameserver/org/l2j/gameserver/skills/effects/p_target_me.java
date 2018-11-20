package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class p_target_me extends Effect
{
	public p_target_me(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(getEffected() == getEffector())
			return false;
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		getEffected().setAggressionTarget(getEffector());
		getEffected().setTarget(getEffector());

		getEffected().abortCast(true, true);
		getEffected().abortAttack(true, true);

		getEffected().getAI().clearNextAction();
	}

	@Override
	public void onExit()
	{
		getEffected().setAggressionTarget(null);
	}
}