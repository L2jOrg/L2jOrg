package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public final class i_my_summon_kill extends i_abstract_effect
{
	public i_my_summon_kill(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void instantUse()
	{
		for(Servitor servitor : getEffected().getServitors())
		{
			if(servitor.isSummon())
				servitor.unSummon(false);
		}
	}
}