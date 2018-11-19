package l2s.gameserver.skills.effects;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

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