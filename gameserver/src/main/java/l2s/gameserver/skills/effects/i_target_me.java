package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public class i_target_me extends i_abstract_effect
{
	public i_target_me(Abnormal abnormal, Env env, EffectTemplate template)
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
	public void instantUse()
	{
		getEffected().setTarget(getEffector());

		getEffected().abortCast(true, true);
		getEffected().abortAttack(true, true);

		getEffected().getAI().clearNextAction();
	}
}