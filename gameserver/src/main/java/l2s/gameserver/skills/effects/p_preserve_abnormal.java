package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class p_preserve_abnormal extends Effect
{
	public p_preserve_abnormal(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		getEffected().setPreserveAbnormal(true);
	}

	@Override
	public void onExit()
	{
		getEffected().setPreserveAbnormal(false);
	}
}