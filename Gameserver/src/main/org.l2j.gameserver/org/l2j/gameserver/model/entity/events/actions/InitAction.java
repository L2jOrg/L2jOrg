package org.l2j.gameserver.model.entity.events.actions;

import org.l2j.gameserver.model.entity.events.EventAction;
import org.l2j.gameserver.model.entity.events.Event;

/**
 * @author VISTALL
 * @date 11:41/30.06.2011
 */
public class InitAction implements EventAction
{
	private String _name;

	public InitAction(String name)
	{
		_name = name;
	}

	@Override
	public void call(Event event)
	{
		event.initAction(_name);
	}
}