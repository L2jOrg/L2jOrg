package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class EffectMuteAttack extends Effect
{
	public EffectMuteAttack(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		if(getEffected().getFlags().getAMuted().start(this))
		{
			getEffected().abortCast(true, true);
			getEffected().abortAttack(true, true);
		}
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getAMuted().stop(this);
	}
}