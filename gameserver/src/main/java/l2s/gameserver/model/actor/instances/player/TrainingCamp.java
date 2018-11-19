package l2s.gameserver.model.actor.instances.player;

import java.util.concurrent.TimeUnit;

import l2s.gameserver.Config;
import l2s.gameserver.instancemanager.TrainingCampManager;
import l2s.gameserver.model.Player;

public class TrainingCamp
{
	public static final long TRAINING_DIVIDER = TimeUnit.SECONDS.toMinutes(Config.TRAINING_CAMP_MAX_DURATION);

	private final String _accountName;
	private final int _objectId;
	private final int _classIndex;
	private final int _level;
	private final long _startTime;
	private long _endTime = 0;

	public TrainingCamp(String accountName, int objectId, int classIndex, int level, long startTime, long endTime)
	{
		_accountName = accountName;
		_objectId = objectId;
		_classIndex = classIndex;
		_level = level;
		_startTime = startTime;
		_endTime = endTime;
	}

	public TrainingCamp(String accountName, int objectId, int classIndex, int level, long startTime)
	{
		_accountName = accountName;
		_objectId = objectId;
		_classIndex = classIndex;
		_level = level;
		_startTime = startTime;
	}

	public int getObjectId()
	{
		return _objectId;
	}

	public int getClassIndex()
	{
		return _classIndex;
	}

	public int getLevel()
	{
		return _level;
	}

	public long getStartTime()
	{
		return _startTime;
	}

	public void setEndTime(long value)
	{
		_endTime = value;
	}

	public long getEndTime()
	{
		return _endTime;
	}

	public boolean isTraining()
	{
		return _endTime == 0;
	}

	public boolean isValid(Player player)
	{
		return Config.TRAINING_CAMP_ENABLE && player.getObjectId() == _objectId && player.getActiveSubClass().getIndex() == _classIndex;
	}

	public int getElapsedTime()
	{
		return Math.max(0, (int) TimeUnit.SECONDS.convert(System.currentTimeMillis() - _startTime, TimeUnit.MILLISECONDS));
	}

	public int getRemainingTime()
	{
		return Math.max(0, getMaxDuration() - getElapsedTime());
	}

	public long getTrainingTime(TimeUnit unit)
	{
		long time = Math.max(0, unit.convert(_endTime - _startTime, TimeUnit.MILLISECONDS));
		time = Math.min(time, unit.convert(getMaxDuration(), TimeUnit.SECONDS));
		return time;
	}

	public int getMaxDuration()
	{
		return Math.max(0, Config.TRAINING_CAMP_MAX_DURATION - TrainingCampManager.getInstance().getTrainingCampDuration(_accountName));
	}
}