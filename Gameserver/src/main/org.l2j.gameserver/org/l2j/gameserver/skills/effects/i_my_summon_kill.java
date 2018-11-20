package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.Servitor;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

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