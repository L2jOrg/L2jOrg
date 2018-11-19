package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
 **/
public class p_block_chat extends Effect
{
	public p_block_chat(Abnormal abnormal, Env env, EffectTemplate template)
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
		getEffected().getPlayer().getFlags().getChatBlocked().start(this);
	}

	@Override
	public void onExit()
	{
		getEffected().getPlayer().getFlags().getChatBlocked().stop(this);
	}
}