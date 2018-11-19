package l2s.gameserver.model.entity.events;

import java.util.Comparator;

/**
 * @author VISTALL
 * @date 14:02/26.08.2011
 */
public class EventComparator implements Comparator<Event>
{
	private static final EventComparator _instance = new EventComparator();

	public static EventComparator getInstance()
	{
		return _instance;
	}

	@Override
	public int compare(Event o1, Event o2)
	{
		EventType type1 = o1.getType();
		EventType type2 = o2.getType();
		if(type1 == type2)
			return o1.hashCode() - o2.hashCode();
		else
			return type1.ordinal() - type2.ordinal();
	}
}