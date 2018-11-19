package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectHPDamPercent extends Effect
{
	public EffectHPDamPercent(final Abnormal abnormal, final Env env, final EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		if(getEffected().isDead())
			return;

		double newHp = (100. - getValue()) * getEffected().getMaxHp() / 100.;
		newHp = Math.min(getEffected().getCurrentHp(), Math.max(0, newHp));
		getEffected().setCurrentHp(newHp, false);
	}
}