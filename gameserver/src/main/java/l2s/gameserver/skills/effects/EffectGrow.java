package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class EffectGrow extends Effect
{
	public EffectGrow(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		if(getEffected().isNpc())
		{
			NpcInstance npc = (NpcInstance) getEffected();
			npc.setCollisionHeightModifier(1.24);
			npc.setCollisionRadiusModifier(1.19);
		}
	}

	@Override
	public void onExit()
	{
		if(getEffected().isNpc())
		{
			NpcInstance npc = (NpcInstance) getEffected();
			npc.setCollisionHeightModifier(1.0);
			npc.setCollisionRadiusModifier(1.0);
		}
	}
}