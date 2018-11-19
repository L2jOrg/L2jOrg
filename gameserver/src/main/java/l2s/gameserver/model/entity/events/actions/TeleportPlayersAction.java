package l2s.gameserver.model.entity.events.actions;

import l2s.gameserver.model.entity.events.EventAction;
import l2s.gameserver.model.entity.events.Event;

/**
 * @author VISTALL
 * @date 0:47/18.05.2011
 */
public class TeleportPlayersAction implements EventAction
{
	private String _name;

	public TeleportPlayersAction(String name)
	{
		_name = name;
	}

	@Override
	public void call(Event event)
	{
		event.teleportPlayers(_name);
	}
}