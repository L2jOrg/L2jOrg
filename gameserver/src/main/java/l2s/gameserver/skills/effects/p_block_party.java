package l2s.gameserver.skills.effects;

import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.stats.Env;
import l2s.gameserver.model.actor.instances.creature.Abnormal;

public final class p_block_party extends Effect
{
	public p_block_party(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if (!getEffected().isPlayer())
			return false;

		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		getEffected().getPlayer().getFlags().getPartyBlocked().start(this);
		getEffected().getPlayer().leaveParty();
	}

	@Override
	public void onExit()
	{
		getEffected().getPlayer().getFlags().getPartyBlocked().stop(this);
	}
}