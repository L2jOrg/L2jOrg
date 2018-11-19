package l2s.gameserver.model.entity.events.objects;

import java.util.Set;

import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import l2s.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 17:55/12.03.2011
 */
public class SiegeToggleNpcObject implements SpawnableObject
{
	private static final long serialVersionUID = 1L;

	private SiegeToggleNpcInstance _toggleNpc;
	private Location _location;

	public SiegeToggleNpcObject(int id, int fakeNpcId, Location loc, int hp, Set<String> set)
	{
		_location = loc;

		_toggleNpc = (SiegeToggleNpcInstance)NpcHolder.getInstance().getTemplate(id).getNewInstance();

		_toggleNpc.initFake(fakeNpcId);
		_toggleNpc.setMaxHp(hp);
		_toggleNpc.setZoneList(set);
	}

	@Override
	public void spawnObject(Event event)
	{
		_toggleNpc.decayFake();

		if(event.isInProgress())
			_toggleNpc.addEvent(event);
		else
			_toggleNpc.removeEvent(event);

		_toggleNpc.setCurrentHp(_toggleNpc.getMaxHp(), true);
		_toggleNpc.spawnMe(_location);
	}

	@Override
	public void despawnObject(Event event)
	{
		_toggleNpc.removeEvent(event);
		_toggleNpc.decayFake();
		_toggleNpc.decayMe();
	}

	@Override
	public void respawnObject(Event event)
	{

	}

	@Override
	public void refreshObject(Event event)
	{
		_toggleNpc.decayFake();

		if(!event.isInProgress())
			_toggleNpc.removeEvent(event);
		else
			_toggleNpc.addEvent(event);

		if(_toggleNpc.getCurrentHp() <= 0)
		{
			_toggleNpc.decayMe();
			_toggleNpc.spawnMe(_location);
		}

		_toggleNpc.setCurrentHp(_toggleNpc.getMaxHp(), true);
	}

	public SiegeToggleNpcInstance getToggleNpc()
	{
		return _toggleNpc;
	}

	public boolean isAlive()
	{
		return _toggleNpc.isVisible();
	}
}