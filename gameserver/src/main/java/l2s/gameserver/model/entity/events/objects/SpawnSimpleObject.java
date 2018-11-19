package l2s.gameserver.model.entity.events.objects;

import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

/**
 * @author VISTALL
 * @date 16:32/14.07.2011
 */
public class SpawnSimpleObject implements SpawnableObject
{
    private static final long serialVersionUID = 1L;

	protected int _npcId;
	private Location _loc;

	protected NpcInstance _npc = null;

	public SpawnSimpleObject(int npcId, Location loc)
	{
		_npcId = npcId;
		_loc = loc;
	}

	@Override
	public void spawnObject(Event event)
	{
		_npc = NpcUtils.spawnSingle(_npcId, _loc, event.getReflection());
		if (_npc != null)
			_npc.addEvent(event);
	}

	@Override
	public void despawnObject(Event event)
	{
		if (_npc != null)
		{
			_npc.removeEvent(event);
			_npc.deleteMe();
			_npc = null;
		}
	}

	@Override
	public void respawnObject(Event event)
	{
		if (_npc != null && !_npc.isVisible())
		{
			_npc.setCurrentHpMp(_npc.getMaxHp(), _npc.getMaxMp(), true);
			_npc.setHeading(_loc.h);
			_npc.setReflection(event.getReflection());
			_npc.spawnMe(_npc.getSpawnedLoc());
		}
	}

	@Override
	public void refreshObject(Event event)
	{

	}

	public NpcInstance getNpc()
	{
		return _npc;
	}

	public Location getLoc()
	{
		return _loc;
	}
}