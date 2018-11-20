package org.l2j.gameserver.skills.effects;

import static org.l2j.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

import org.l2j.gameserver.model.Servitor;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class EffectBetray extends Effect
{
	public EffectBetray(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		if(getEffected() != null && getEffected().isSummon())
		{
			Servitor summon = (Servitor) getEffected();
			summon.setDepressed(true);
			summon.getAI().Attack(summon.getPlayer(), true, false);
		}
	}

	@Override
	public void onExit()
	{
		if(getEffected() != null && getEffected().isSummon())
		{
			Servitor summon = (Servitor) getEffected();
			summon.setDepressed(false);
			summon.getAI().setIntention(AI_INTENTION_ACTIVE);
		}
	}
}