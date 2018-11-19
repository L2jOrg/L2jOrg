package l2s.gameserver.skills.effects;

import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
 */
public final class i_delete_hate extends i_abstract_effect
{
	public i_delete_hate(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(!getEffected().isMonster())
			return false;

		return super.checkCondition();
	}

	@Override
	public void instantUse()
	{
		MonsterInstance monster = (MonsterInstance) getEffected();
		monster.getAggroList().clear(true);
		if(monster.getAI() instanceof DefaultAI)
			((DefaultAI) monster.getAI()).setGlobalAggro(System.currentTimeMillis() + monster.getParameter("globalAggro", 10000L));	//TODO: Check this.
		monster.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}
}