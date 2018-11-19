package l2s.gameserver.skills.effects;

import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.stats.Env;
import l2s.gameserver.model.actor.instances.creature.Abnormal;

public final class p_block_move extends Effect
{
	public p_block_move(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getMoveBlocked().start(this);
		getEffected().stopMove();
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getMoveBlocked().stop(this);
	}
}