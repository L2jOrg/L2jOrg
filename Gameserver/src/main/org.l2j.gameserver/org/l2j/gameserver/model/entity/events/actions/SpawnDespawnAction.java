package org.l2j.gameserver.model.entity.events.actions;

import org.l2j.gameserver.model.entity.events.EventAction;
import org.l2j.gameserver.model.entity.events.Event;

/**
 * @author VISTALL
 * @date 17:05/10.12.2010
 */
public class SpawnDespawnAction implements EventAction
{
	private final boolean _spawn;
	private final String _name;

	public SpawnDespawnAction(String name, boolean spawn)
	{
		_spawn = spawn;
		_name = name;
	}

	@Override
	public void call(Event event)
	{
		event.spawnAction(_name, _spawn);
	}
}