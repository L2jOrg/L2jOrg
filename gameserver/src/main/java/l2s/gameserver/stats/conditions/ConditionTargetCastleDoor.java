package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.stats.Env;

public class ConditionTargetCastleDoor extends Condition
{
	private final boolean _isCastleDoor;

	public ConditionTargetCastleDoor(boolean isCastleDoor)
	{
		_isCastleDoor = isCastleDoor;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return env.target instanceof DoorInstance == _isCastleDoor;
	}
}
