package l2s.gameserver.model.quest.startcondition.impl;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.quest.startcondition.ICheckStartCondition;

/**
 * @author : Ragnarok
 * @date : 07.02.12  3:29
 */
public final class QuestCompletedCondition implements ICheckStartCondition
{
	private final int _questId;

	public QuestCompletedCondition(int questId)
	{
		_questId = questId;
	}

	@Override
	public final boolean checkCondition(Player player)
	{
		return player.isQuestCompleted(_questId);
	}
}