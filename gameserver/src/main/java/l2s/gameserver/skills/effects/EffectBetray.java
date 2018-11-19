package l2s.gameserver.skills.effects;

import static l2s.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

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