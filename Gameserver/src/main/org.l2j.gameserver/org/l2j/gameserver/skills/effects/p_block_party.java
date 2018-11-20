package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.templates.skill.EffectTemplate;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;

public final class p_block_party extends Effect
{
	public p_block_party(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if (!getEffected().isPlayer())
			return false;

		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		getEffected().getPlayer().getFlags().getPartyBlocked().start(this);
		getEffected().getPlayer().leaveParty();
	}

	@Override
	public void onExit()
	{
		getEffected().getPlayer().getFlags().getPartyBlocked().stop(this);
	}
}