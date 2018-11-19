package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectMuteAll extends Effect
{
	public EffectMuteAll(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getMuted().start(this);
		getEffected().getFlags().getPMuted().start(this);
		getEffected().abortCast(true, true);
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getMuted().stop(this);
		getEffected().getFlags().getPMuted().stop(this);
	}
}