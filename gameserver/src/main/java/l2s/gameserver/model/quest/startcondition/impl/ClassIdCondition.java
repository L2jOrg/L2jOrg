package l2s.gameserver.model.quest.startcondition.impl;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.quest.startcondition.ICheckStartCondition;

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