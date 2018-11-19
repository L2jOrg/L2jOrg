package l2s.gameserver.model.entity.events;

import l2s.commons.threading.RunnableImpl;

/**
 * @author VISTALL
 * @date  18:02/10.12.2010
 */
public class EventTimeTask extends RunnableImpl
{
	private final Event _event;
	private final int _time;

	public EventTimeTask(Event event, int time)
	{
		_event = event;
		_time = time;
	}

	@Override
	public void runImpl() throws Exception
	{
		_event.timeActions(_time);
	}
}