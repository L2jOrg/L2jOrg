package l2s.gameserver.model.entity.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author VISTALL
 * @date 10:27/24.02.2011
 */
public abstract class EventOwner
{
	private Set<Event> _events = new ConcurrentSkipListSet<Event>(EventComparator.getInstance());

	@SuppressWarnings("unchecked")
	public <E extends Event> E getEvent(Class<E> eventClass)
	{
		for(Event e : _events)
		{
			if(e.getClass() == eventClass)    // fast hack
				return (E)e;
			if(eventClass.isAssignableFrom(e.getClass()))    //FIXME [VISTALL]    какойто другой способ определить
				return (E)e;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <E extends Event> List<E> getEvents(Class<E> eventClass)
	{
		List<E> events = new ArrayList<E>();
		for(Event e : _events)
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

	public boolean containsEvent(Event event)
	{
		return _events.contains(event);
	}

	public boolean containsEvent(Class<? extends Event> eventClass)
	{
		for(Event e : _events)
		{
			if(e.getClass() == eventClass) // fast hack
				return true;

			if(eventClass.isAssignableFrom(e.getClass())) //FIXME [VISTALL]
				return true;
		}
		return false;
	}

	public void addEvent(Event event)
	{
		_events.add(event);
	}

	public void removeEvent(Event event)
	{
		_events.remove(event);
	}

	public void removeEvents(Class<? extends Event> eventClass)
	{
		for(Event e : _events)
		{
			if(e.getClass() == eventClass)    // fast hack
				_events.remove(e);
			else if(eventClass.isAssignableFrom(e.getClass()))    //FIXME [VISTALL]    какойто другой способ определить
				_events.remove(e);
		}

	}

	public Set<Event> getEvents()
	{
		return _events;
	}
}
