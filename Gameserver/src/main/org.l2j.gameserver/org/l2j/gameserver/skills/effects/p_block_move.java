package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.templates.skill.EffectTemplate;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;

public final class p_block_move extends Effect
{
	public p_block_move(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getMoveBlocked().start(this);
		getEffected().stopMove();
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getMoveBlocked().stop(this);
	}
}