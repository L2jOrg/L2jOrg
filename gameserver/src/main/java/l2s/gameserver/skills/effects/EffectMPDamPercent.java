package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectMPDamPercent extends Effect
{
	public EffectMPDamPercent(final Abnormal abnormal, final Env env, final EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		if(getEffected().isDead())
			return;

		double newMp = (100. - getValue()) * getEffected().getMaxMp() / 100.;
		newMp = Math.min(getEffected().getCurrentMp(), Math.max(0, newMp));
		getEffected().setCurrentMp(newMp);
	}
}