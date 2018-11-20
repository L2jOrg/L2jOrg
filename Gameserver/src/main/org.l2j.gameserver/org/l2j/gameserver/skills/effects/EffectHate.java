package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class EffectHate extends Effect
{
	public EffectHate(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean onActionTime()
	{
		if(getEffected().isNpc() && getEffected().isMonster())
			getEffected().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getEffector(), getValue());
		return true;
	}
}