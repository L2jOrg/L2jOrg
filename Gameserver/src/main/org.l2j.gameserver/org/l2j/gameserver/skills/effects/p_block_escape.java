package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.templates.skill.EffectTemplate;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;

public class p_block_escape extends Effect
{
	public p_block_escape(Abnormal abnormal, Env env, EffectTemplate template)
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
	public void onStart()
	{
		getEffected().getPlayer().getFlags().getEscapeBlocked().start(this);
	}

	@Override
	public void onExit()
	{
		getEffected().getPlayer().getFlags().getEscapeBlocked().stop(this);
	}
}