package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class EffectParalyze extends Effect
{
	public EffectParalyze(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(getEffected().isParalyzeImmune())
			return false;
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getParalyzed().start(this);
		getEffected().abortAttack(true, true);
		getEffected().abortCast(true, true);
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getParalyzed().stop(this);
	}
}