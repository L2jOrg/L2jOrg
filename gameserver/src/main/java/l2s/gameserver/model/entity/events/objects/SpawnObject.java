package l2s.gameserver.model.entity.events.objects;

import l2s.gameserver.instancemanager.SpawnManager;
import l2s.gameserver.model.entity.events.Event;

/**
 * @author Bonux
 * Данный обьект нужен для спавна обычных NPC в дефолтный мир.
 **/
public class SpawnObject implements SpawnableObject
{
	private static final long serialVersionUID = 1L;

	private final String _name;

	public SpawnObject(String name)
	{
		_name = name;
	}

	@Override
	public void spawnObject(Event event)
	{
		SpawnManager.getInstance().spawn(_name, false);
	}

	@Override
	public void respawnObject(Event event)
	{
		SpawnManager.getInstance().despawn(_name);
		SpawnManager.getInstance().spawn(_name, false);
	}

	@Override
	public void despawnObject(Event event)
	{
		SpawnManager.getInstance().despawn(_name);
	}

	@Override
	public void refreshObject(Event event)
	{
		//
	}
}