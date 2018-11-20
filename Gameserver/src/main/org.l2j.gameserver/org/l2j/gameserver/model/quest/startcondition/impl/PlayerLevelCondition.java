package org.l2j.gameserver.model.quest.startcondition.impl;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.quest.startcondition.ICheckStartCondition;

/**
 * @author : Ragnarok
 * @date : 07.02.12  3:22
 */
public final class PlayerLevelCondition implements ICheckStartCondition
{
	private final int _min;
	private final int _max;

	public PlayerLevelCondition(int min, int max)
	{
		_min = min;
		_max = max;
	}

	@Override
	public final boolean checkCondition(Player player)
	{
		return player.getLevel() >= _min && player.getLevel() <= _max;
	}
}