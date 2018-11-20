package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.model.instances.MonsterInstance;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
 */
public final class i_delete_hate_of_me extends i_abstract_effect
{
	public i_delete_hate_of_me(Abnormal abnormal, Env env, EffectTemplate template)
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
		monster.getAggroList().remove(getEffector(), true);
		monster.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}
}