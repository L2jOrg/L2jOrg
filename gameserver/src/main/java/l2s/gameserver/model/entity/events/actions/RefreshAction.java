package l2s.gameserver.model.entity.events.actions;

import l2s.gameserver.model.entity.events.EventAction;
import l2s.gameserver.model.entity.events.Event;

/**
 * @author VISTALL
 * @date 23:45/09.03.2011
 */
public class RefreshAction implements EventAction
{
	private final String _name;

	public RefreshAction(String name)
	{
		_name = name;
	}

	@Override
	public void call(Event event)
	{
		event.refreshAction(_name);
	}
}