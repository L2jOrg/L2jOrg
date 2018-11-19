package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * Target is immune to death.
 *
 * @author Yorie
 */
public final class EffectDeathImmunity extends Effect
{
	public EffectDeathImmunity(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getDeathImmunity().start(this);
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getDeathImmunity().stop(this);
	}
}