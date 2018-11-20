package org.l2j.gameserver.model.quest.startcondition.impl;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.ClassType;
import org.l2j.gameserver.model.quest.startcondition.ICheckStartCondition;

public class ClassTypeCondition implements ICheckStartCondition
{
	private final ClassType _type;

	public ClassTypeCondition(ClassType type)
	{
		_type = type;
	}

	@Override
	public boolean checkCondition(Player player)
	{
		return player.getClassId().isOfType(_type);
	}
}