package org.l2j.gameserver.stats.conditions;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.stats.Env;

/**
 * @author Bonux
**/
public class ConditionPlayerQuestState extends Condition
{
	private final int _questId;
	private final int _cond;

	public ConditionPlayerQuestState(int questId, int cond)
	{
		_questId = questId;
		_cond = cond;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return false;

		Player player = env.character.getPlayer();
		QuestState qs = player.getQuestState(_questId);
		if(qs == null)
			return false;

		return qs.getCond() == _cond;
	}
}