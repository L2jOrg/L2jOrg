package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class i_get_agro extends i_abstract_effect
{
	public i_get_agro(Abnormal abnormal, Env env, EffectTemplate template)
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
		getEffected().getAI().Attack(getEffector(), false, false);
	}
}