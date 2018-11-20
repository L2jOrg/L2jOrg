package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class EffectEnervation extends Effect
{
	public EffectEnervation(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		if(getEffected().isNpc())
			((NpcInstance) getEffected()).setParameter("DebuffIntention", 0.5);
	}

	@Override
	public void onExit()
	{
		if(getEffected().isNpc())
			((NpcInstance) getEffected()).setParameter("DebuffIntention", 1.);
	}
}