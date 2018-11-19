package l2s.gameserver.model.entity.events.objects;

import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.model.instances.DoorInstance;

/**
 * @author VISTALL
 * @date  17:29/10.12.2010
 */
public class DoorObject implements SpawnableObject, InitableObject
{
	private static final long serialVersionUID = 1L;

	private int _id;
	private DoorInstance _door;

	private boolean _weak;

	public DoorObject(int id)
	{
		_id = id;
	}

	@Override
	public void initObject(Event e)
	{
		_door = e.getReflection().getDoor(_id);
	}

	@Override
	public void spawnObject(Event event)
	{
		refreshObject(event);
	}

	@Override
	public void despawnObject(Event event)
	{
		Reflection ref = event.getReflection();
		if(ref.isMain())
		{
			refreshObject(event);
		}
		else
		{
			//TODO [VISTALL] удалить двери
		}
	}

	@Override
	public void respawnObject(Event event)
	{
		//
	}

	@Override
	public void refreshObject(Event event)
	{
		if(!event.isInProgress())
			_door.removeEvent(event);
		else
			_door.addEvent(event);

		if(_door.getCurrentHp() <= 0)
		{
			_door.decayMe();
			_door.spawnMe();
		}

		_door.setCurrentHp(_door.getMaxHp() * (isWeak() ? 0.5 : 1.), true);
		close(event);
	}

	public int getId()
	{
		return _id;
	}

	public int getUId()
	{
		return _door.getDoorId();
	}

	public int getUpgradeValue()
	{
		return _door.getUpgradeHp();
	}

	public void setUpgradeValue(Event event, int val)
	{
		_door.setUpgradeHp(val);
		refreshObject(event);
	}

	public void open(Event e)
	{
		_door.openMe(null, !e.isInProgress());
	}

	public void close(Event e)
	{
		_door.closeMe(null, !e.isInProgress());
	}

	public DoorInstance getDoor()
	{
		return _door;
	}

	public boolean isWeak()
	{
		return _weak;
	}

	public void setWeak(boolean weak)
	{
		_weak = weak;
	}
}