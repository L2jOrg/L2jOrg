package l2s.gameserver.model.entity.events.impl;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;

public abstract class AbstractDuelEvent extends SingleMatchEvent
{
	public AbstractDuelEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	protected AbstractDuelEvent(int id, String name)
	{
		super(id, name);
	}

	public abstract boolean canDuel(Player player, Player target, boolean first);

	public abstract void askDuel(Player player, Player target, int arenaId);

	public abstract void createDuel(Player player, Player target, int arenaId);
}