package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectCPDamPercent extends Effect
{
	public EffectCPDamPercent(final Abnormal abnormal, final Env env, final EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		if(getEffected().isDead() || !getEffected().isPlayer())
			return;

		double newCp = (100. - getValue()) * getEffected().getMaxCp() / 100.;
		newCp = Math.min(getEffected().getCurrentCp(), Math.max(0, newCp));
		getEffected().setCurrentCp(newCp);
	}
}