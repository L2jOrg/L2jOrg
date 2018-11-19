package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class EffectHealBlock extends Effect
{
	public EffectHealBlock(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(getEffected().isHealBlocked())
			return false;
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getHealBlocked().start(this);
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getHealBlocked().stop(this);
	}
}