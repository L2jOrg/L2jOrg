package l2s.gameserver.skills.effects;

import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.stats.Env;
import l2s.gameserver.model.actor.instances.creature.Abnormal;

public class p_block_escape extends Effect
{
	public p_block_escape(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(!getEffected().isPlayer())
			return false;

		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		getEffected().getPlayer().getFlags().getEscapeBlocked().start(this);
	}

	@Override
	public void onExit()
	{
		getEffected().getPlayer().getFlags().getEscapeBlocked().stop(this);
	}
}