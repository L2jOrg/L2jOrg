package l2s.gameserver.model.entity.events.actions;

import l2s.gameserver.model.entity.events.EventAction;
import l2s.gameserver.model.entity.events.Event;

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