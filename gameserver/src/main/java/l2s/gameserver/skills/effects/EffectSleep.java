package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class EffectSleep extends Effect
{
	public EffectSleep(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getSleeping().start(this);
		getEffected().abortAttack(true, true);
		getEffected().abortCast(true, true);
		getEffected().stopMove();
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getSleeping().stop(this);
	}
}