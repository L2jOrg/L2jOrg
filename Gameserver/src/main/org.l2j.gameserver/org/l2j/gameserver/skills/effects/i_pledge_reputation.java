package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class i_pledge_reputation extends i_abstract_effect
{
	public i_pledge_reputation(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(getEffected().getClan() == null)
			return false;
		return super.checkCondition();
	}

	@Override
	public void instantUse()
	{
		getEffected().getClan().incReputation((int) getValue(), false, "Using skill ID[" + getSkill().getId() + "] LEVEL[" + getSkill().getLevel() + "]");
	}
}