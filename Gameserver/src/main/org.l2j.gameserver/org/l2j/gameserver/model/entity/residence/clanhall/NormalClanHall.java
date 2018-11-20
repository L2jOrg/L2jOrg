package org.l2j.gameserver.model.entity.residence.clanhall;

import org.l2j.gameserver.model.entity.residence.ClanHall;
import org.l2j.gameserver.templates.StatsSet;

public abstract class NormalClanHall extends ClanHall
{
	private static final long serialVersionUID = 1L;

	public NormalClanHall(StatsSet set)
	{
		super(set);
	}
}