package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

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