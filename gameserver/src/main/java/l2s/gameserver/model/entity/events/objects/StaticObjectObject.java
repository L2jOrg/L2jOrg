package l2s.gameserver.model.entity.events.objects;

import l2s.gameserver.data.xml.holder.StaticObjectHolder;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.model.instances.StaticObjectInstance;

/**
 * @author VISTALL
 * @date 22:50/09.03.2011
 */
public class StaticObjectObject implements SpawnableObject
{
	private static final long serialVersionUID = 1L;

	private int _uid;
	private StaticObjectInstance _instance;

	public StaticObjectObject(int id)
	{
		_uid = id;
	}

	@Override
	public void spawnObject(Event event)
	{
		_instance = StaticObjectHolder.getInstance().getObject(_uid);
	}

	@Override
	public void despawnObject(Event event)
	{
		//
	}

	@Override
	public void respawnObject(Event event)
	{

	}

	@Override
	public void refreshObject(Event event)
	{
		if(!event.isInProgress())
			_instance.removeEvent(event);
		else
			_instance.addEvent(event);
	}

	public void setMeshIndex(int id)
	{
		_instance.setMeshIndex(id);
		_instance.broadcastInfo(false);
	}

	public int getUId()
	{
		return _uid;
	}
}