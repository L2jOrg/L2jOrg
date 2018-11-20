package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class EffectFlyUp extends Effect
{
	public EffectFlyUp(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(getEffected().isPeaceNpc())
			return false;
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getFlyUp().start(this);

		getEffected().abortAttack(true, true);
		getEffected().abortCast(true, true);
		getEffected().stopMove();

		getEffected().getAI().notifyEvent(CtrlEvent.EVT_FLY_UP, getEffected());
	}

	@Override
	public void onExit()
	{
		if(getEffected().getFlags().getFlyUp().stop(this))
		{
			if(!getEffected().isPlayer())
				getEffected().getAI().notifyEvent(CtrlEvent.EVT_THINK);
		}
	}
}