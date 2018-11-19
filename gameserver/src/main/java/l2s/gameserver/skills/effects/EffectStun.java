package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class EffectStun extends Effect
{
	public EffectStun(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getStunned().start(this);
		getEffected().abortAttack(true, true);
		getEffected().abortCast(true, true);
		getEffected().stopMove();
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getStunned().stop(this);
	}
}