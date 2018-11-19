package l2s.gameserver.model.entity.residence.clanhall;

import l2s.gameserver.model.entity.residence.ClanHall;
import l2s.gameserver.templates.StatsSet;

public abstract class NormalClanHall extends ClanHall
{
	private static final long serialVersionUID = 1L;

	public NormalClanHall(StatsSet set)
	{
		super(set);
	}
}