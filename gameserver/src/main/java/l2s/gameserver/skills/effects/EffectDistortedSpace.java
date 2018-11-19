package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public final class EffectDistortedSpace extends Effect
{
	public EffectDistortedSpace(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getDistortedSpace().start(this);
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getDistortedSpace().stop(this);
	}
}