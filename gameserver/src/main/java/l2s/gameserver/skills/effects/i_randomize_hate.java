package l2s.gameserver.skills.effects;

import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.AggroList;
import l2s.gameserver.model.AggroList.AggroInfo;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

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