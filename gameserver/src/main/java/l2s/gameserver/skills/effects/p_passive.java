package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public class p_passive extends Effect
{
	public p_passive(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		if(getEffected().isNpc())
		{
			NpcInstance npc = (NpcInstance) getEffected();
			npc.setUnAggred(true);
		}
	}

	@Override
	public void onExit()
	{
		if(getEffected().isNpc())
			((NpcInstance) getEffected()).setUnAggred(false);
	}
}