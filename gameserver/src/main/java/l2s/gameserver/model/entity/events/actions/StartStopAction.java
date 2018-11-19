package l2s.gameserver.model.entity.events.actions;

import l2s.gameserver.model.entity.events.EventAction;
import l2s.gameserver.model.entity.events.Event;

/**
 * @author VISTALL
 * @date  16:29/10.12.2010
 */
public class StartStopAction implements EventAction
{
	public static final String EVENT = "event";

	private final String _name;
	private final boolean _start;

	public StartStopAction(String name, boolean start)
	{
		_name = name;
		_start = start;
	}

	@Override
	public void call(Event event)
	{
		event.action(_name, _start);
	}

	public String getName()
	{
		return _name;
	}

	public boolean isStart()
	{
		return _start;
	}
}