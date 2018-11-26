package org.l2j.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.List;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.events.Event;
import org.l2j.gameserver.model.entity.events.EventType;

/**
 * @author VISTALL
 * @date  12:55/10.12.2010
 */
public final class EventHolder extends AbstractHolder
{
	private static final EventHolder _instance = new EventHolder();
	private final TIntObjectMap<Event> _events = new TIntObjectHashMap<>();

	public static EventHolder getInstance()
	{
		return _instance;
	}

	public void addEvent(Event event)
	{
		_events.put(getHash(event.getType(), event.getId()), event);
	}

	@SuppressWarnings("unchecked")
	public <E extends Event> E getEvent(EventType type, int id)
	{
		return (E) _events.get(getHash(type, id));
	}

	@SuppressWarnings("unchecked")
	public <E extends Event> List<E> getEvents(EventType type)
	{
		List<E> events = new ArrayList<E>();
		for(Event e : _events.valueCollection())
		{
			if(e.getType() == type)
				events.add((E) e);
		}
		return events;
	}

	@SuppressWarnings("unchecked")
	public <E extends Event> List<E> getEvents(Class<E> eventClass)
	{
		List<E> events = new ArrayList<E>();
		for(Event e : _events.valueCollection())
		{
			if(e.getClass() == eventClass) // fast hack
			{
				events.add((E) e);
				continue;
			}
			if(eventClass.isAssignableFrom(e.getClass())) //FIXME [VISTALL] 
			{
				events.add((E) e);
				continue;
			}
		}
		return events;
	}

	public void findEvent(Player player)
	{
		for(Event event : _events.valueCollection())
			event.findEvent(player);
	}

	public void callInit()
	{
		for(Event event : _events.valueCollection())
			event.initEvent();
	}

	private static int getHash(EventType type, int id)
	{
		return type.ordinal() * 100000 + id;
	}

	@Override
	public int size()
	{
		return _events.size();
	}

	@Override
	public void clear()
	{
		_events.clear();
	}
}