package l2s.gameserver.model.quest.startcondition.impl;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.quest.startcondition.ICheckStartCondition;

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