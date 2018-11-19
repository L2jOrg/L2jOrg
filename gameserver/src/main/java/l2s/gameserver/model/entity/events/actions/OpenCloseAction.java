package l2s.gameserver.model.entity.events.actions;

import l2s.gameserver.model.entity.events.EventAction;
import l2s.gameserver.model.entity.events.Event;

/**
 * @author VISTALL
 * @date 17:07/09.03.2011
 */
public class OpenCloseAction implements EventAction
{
	private final boolean _open;
	private final String _name;

	public OpenCloseAction(boolean open, String name)
	{
		_open = open;
		_name = name;
	}

	@Override
	public void call(Event event)
	{
		event.doorAction(_name, _open);
	}
}