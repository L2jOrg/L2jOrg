package org.l2j.gameserver.skills.effects;

import java.util.List;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.AggroList;
import org.l2j.gameserver.model.AggroList.AggroInfo;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.model.instances.MonsterInstance;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class i_randomize_hate extends i_abstract_effect
{
	public i_randomize_hate(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		return getEffected().isMonster();
	}

	@Override
	public void instantUse()
	{
		MonsterInstance monster = (MonsterInstance) getEffected();
		Creature mostHated = monster.getAggroList().getMostHated(monster.getAI().getMaxHateRange());
		if(mostHated == null)
			return;

		AggroInfo mostAggroInfo = monster.getAggroList().get(mostHated);
		List<Creature> hateList = monster.getAggroList().getHateList(monster.getAI().getMaxHateRange());
		hateList.remove(mostHated);

		if(!hateList.isEmpty())
		{
			AggroInfo newAggroInfo = monster.getAggroList().get(hateList.get(Rnd.get(hateList.size())));
			if(newAggroInfo == null)
				return;

			int oldHate = newAggroInfo.hate;

			newAggroInfo.hate = mostAggroInfo.hate;
			mostAggroInfo.hate = oldHate;
		}
	}
}