package org.l2j.gameserver.templates.fakeplayer;

import org.l2j.gameserver.templates.fakeplayer.actions.OrdinaryActions;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

public class FakePlayerActionsHolder
{
	private final IntObjectMap<OrdinaryActions> _actionsMap = new HashIntObjectMap<OrdinaryActions>();

	public void addAction(int id, OrdinaryActions action)
	{
		_actionsMap.put(id, action);
	}

	public OrdinaryActions getAction(int id)
	{
		return _actionsMap.get(id);
	}
}