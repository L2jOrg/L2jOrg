package org.l2j.gameserver.model.quest.startcondition.impl;

import org.l2j.commons.lang.ArrayUtils;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.ClassLevel;
import org.l2j.gameserver.model.quest.startcondition.ICheckStartCondition;

/**
 * @author : Ragnarok
 * @date : 02.04.12  21:50
 */
public class ClassLevelCondition implements ICheckStartCondition
{
	private final ClassLevel[] _classLevels;

	public ClassLevelCondition(ClassLevel... classLevels)
	{
		_classLevels = classLevels;
	}

	@Override
	public boolean checkCondition(Player player)
	{
		return ArrayUtils.contains(_classLevels, player.getClassLevel());
	}
}