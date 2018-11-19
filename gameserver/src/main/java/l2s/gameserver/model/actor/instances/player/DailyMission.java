package l2s.gameserver.model.actor.instances.player;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.dailymissions.DailyMissionStatus;
import l2s.gameserver.templates.dailymissions.DailyMissionTemplate;

/**
 * @author Bonux
 */
public class DailyMission implements Comparable<DailyMission>
{
	private final Player _owner;
	private final DailyMissionTemplate _template;
	private final boolean _finallyCompleted;
	private boolean _completed;
	private int _value;

	public DailyMission(Player owner, DailyMissionTemplate template, boolean completed, int value)
	{
		_owner = owner;
		_template = template;
		_completed = completed;
		_value = value;
		_finallyCompleted = completed && !_template.getHandler().isReusable();
	}

	public int getId()
	{
		return _template.getId();
	}

	public DailyMissionTemplate getTemplate()
	{
		return _template;
	}

	public void setCompleted(boolean value)
	{
		_completed = value;
	}

	public boolean isCompleted()
	{
		if(_completed)
		{
			if(!_template.getHandler().isReusable())
				return true;

			long reuseTime = _template.getHandler().getReusePattern().next(getValue() * 1000L);
			if(reuseTime > System.currentTimeMillis())
				return true;
		}
		return false;
	}

	public boolean isFinallyCompleted()
	{
		return _finallyCompleted;
	}

	public void setValue(int value)
	{
		_value = value;
	}

	public int getValue()
	{
		return _value;
	}

	public DailyMissionStatus getStatus()
	{
		if(!Config.EX_USE_TO_DO_LIST)
			return DailyMissionStatus.NOT_AVAILABLE;
		if(_owner.getLevel() < _template.getMinLevel() || _owner.getLevel() > _template.getMaxLevel())
			return DailyMissionStatus.NOT_AVAILABLE;
		return _template.getHandler().getStatus(_owner, this);
	}

	public int getRequiredProgress()
	{
		return _template.getValue();
	}

	public int getCurrentProgress()
	{
		if(!Config.EX_USE_TO_DO_LIST)
			return 0;

		if(isCompleted())
			return getRequiredProgress();

		return _template.getHandler().getProgress(_owner, this);
	}

	@Override
	public String toString()
	{
		return "DailyMission[id=" + _template.getId() + ", completed=" + _completed + ", value=" + _value + "]";
	}

	@Override
	public int compareTo(DailyMission o)
	{
		return getId() - o.getId();
	}
}