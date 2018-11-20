package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class i_sp extends i_abstract_effect
{
	public i_sp(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(!getEffected().isPlayer())
			return false;
		return super.checkCondition();
	}

	@Override
	public void instantUse()
	{
		getEffected().getPlayer().addExpAndSp(0, (int) getValue());
	}
}