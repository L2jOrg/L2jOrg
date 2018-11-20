package org.l2j.gameserver.model.entity.residence.clanhall;

import org.l2j.gameserver.model.entity.residence.ClanHallType;
import org.l2j.gameserver.templates.StatsSet;

public class OtherClanHall extends NormalClanHall
{
	private static final long serialVersionUID = 1L;

	public OtherClanHall(StatsSet set)
	{
		super(set);
	}

	@Override
	public ClanHallType getClanHallType()
	{
		return ClanHallType.OTHER;
	}
}