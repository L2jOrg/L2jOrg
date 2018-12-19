package org.l2j.gameserver.templates.fakeplayer;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.l2j.gameserver.templates.fakeplayer.actions.OrdinaryActions;

public class FakePlayerActionsHolder
{
	private final TIntObjectMap<OrdinaryActions> _actionsMap = new TIntObjectHashMap<>();

	public void addAction(int id, OrdinaryActions action)
	{
		_actionsMap.put(id, action);
	}

	public OrdinaryActions getAction(int id)
	{
		return _actionsMap.get(id);
	}
}