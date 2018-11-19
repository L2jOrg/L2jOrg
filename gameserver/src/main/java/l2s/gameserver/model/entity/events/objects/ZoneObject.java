package l2s.gameserver.model.entity.events.objects;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.Event;

/**
 * @author VISTALL
 * @date 11:40/30.06.2011
 */
public class ZoneObject implements InitableObject
{
	private static final long serialVersionUID = 1L;

	private String _name;
	private Zone _zone;

	public ZoneObject(String name)
	{
		_name = name;
	}

	@Override
	public void initObject(Event e)
	{
		Reflection r = e.getReflection();

		_zone = r.getZone(_name);
	}

	public void setActive(boolean a)
	{
		_zone.setActive(a);
	}

	public void setActive(boolean a, Event event)
	{
		setActive(a);

		if(a)
			_zone.addEvent(event);
		else
			_zone.removeEvent(event);
	}

	public Zone getZone()
	{
		return _zone;
	}

	public List<Player> getInsidePlayers()
	{
		return _zone.getInsidePlayers();
	}

	public boolean checkIfInZone(Creature c)
	{
		return _zone.checkIfInZone(c);
	}
}