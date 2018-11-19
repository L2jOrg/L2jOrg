package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class EffectCPDrain extends Effect
{
	private final boolean _percent;

	public EffectCPDrain(final Abnormal abnormal, final Env env, final EffectTemplate template)
	{
		super(abnormal, env, template);
		_percent = template.getParam().getBool("percent", false);
	}

	@Override
	public void onStart()
	{
		if(getEffected().isDead())
			return;

		if(getEffected() == getEffector())
			return;

		double drained = getValue();
		if(_percent)
			drained = getEffected().getMaxCp() / 100. * drained;

		drained = Math.min(drained, getEffected().getCurrentCp());
		if(drained <= 0)
			return;

		getEffected().setCurrentCp(Math.max(0., getEffected().getCurrentCp() - drained));

		double newCp = getEffector().getCurrentCp() + drained;
		newCp = Math.max(0, Math.min(newCp, getEffector().getMaxCp() / 100. * getEffector().calcStat(Stats.CP_LIMIT, null, null)));

		double addToCp = newCp - getEffected().getCurrentCp();
		if(addToCp > 0)
			getEffector().setCurrentCp(newCp);
	}
}