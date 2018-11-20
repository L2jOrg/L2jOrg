package org.l2j.gameserver.model.entity.events.actions;

import org.l2j.gameserver.model.entity.events.EventAction;
import org.l2j.gameserver.model.entity.events.Event;

/**
 * @author VISTALL
 * @date 15:44/28.04.2011
 */
public class ActiveDeactiveAction implements EventAction
{
	private final boolean _active;
	private final String _name;

	public ActiveDeactiveAction(boolean active, String name)
	{
		_active = active;
		_name = name;
	}

	@Override
	public void call(Event event)
	{
		event.zoneAction(_name, _active);
	}
}