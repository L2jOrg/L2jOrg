package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class EffectDestroySummon extends Effect
{
	public EffectDestroySummon(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(!getEffected().isSummon())
			return false;
		return super.checkCondition();
	}

	@Override
	public boolean onActionTime()
	{
		((Servitor) getEffected()).unSummon(false);
		return true;
	}
}