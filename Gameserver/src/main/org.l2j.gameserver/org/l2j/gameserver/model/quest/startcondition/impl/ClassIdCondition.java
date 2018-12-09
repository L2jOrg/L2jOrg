package org.l2j.gameserver.model.quest.startcondition.impl;

import org.l2j.commons.lang.ArrayUtils;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.startcondition.ICheckStartCondition;

/**
 * @author : Ragnarok
 * @date : 02.04.12  21:50
 */
public class ClassIdCondition implements ICheckStartCondition
{
	private int[] _classId;

	public ClassIdCondition(int... classId)
	{
		_classId = classId;
	}

	public ClassIdCondition(ClassId... classIds)
	{
		_classId = new int[classIds.length];
		for(int i = 0; i < classIds.length; i++)
			_classId[i] = classIds[i].getId();
	}

	@Override
	public boolean checkCondition(Player player)
	{
		return ArrayUtils.contains(_classId, player.getClassId().getId());
	}
}