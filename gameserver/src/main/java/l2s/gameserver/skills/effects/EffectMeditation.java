package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * Иммобилизует и парализует на время действия.
 * @author Diamond
 * @date 24.07.2007
 * @time 5:32:46
 */
public final class EffectMeditation extends Effect
{
	public EffectMeditation(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getParalyzed().start(this);
		getEffected().setMeditated(true);
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getParalyzed().stop(this);
		getEffected().setMeditated(false);
	}
}