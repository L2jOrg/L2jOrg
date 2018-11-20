package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class EffectMuteAll extends Effect
{
	public EffectMuteAll(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getMuted().start(this);
		getEffected().getFlags().getPMuted().start(this);
		getEffected().abortCast(true, true);
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getMuted().stop(this);
		getEffected().getFlags().getPMuted().stop(this);
	}
}