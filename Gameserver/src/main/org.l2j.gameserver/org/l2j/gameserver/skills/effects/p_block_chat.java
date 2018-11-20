package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
 **/
public class p_block_chat extends Effect
{
	public p_block_chat(Abnormal abnormal, Env env, EffectTemplate template)
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
		getEffected().getPlayer().getFlags().getChatBlocked().start(this);
	}

	@Override
	public void onExit()
	{
		getEffected().getPlayer().getFlags().getChatBlocked().stop(this);
	}
}