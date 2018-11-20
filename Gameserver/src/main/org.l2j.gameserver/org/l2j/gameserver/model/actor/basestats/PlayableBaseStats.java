package org.l2j.gameserver.model.actor.basestats;

import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.stats.Stats;

/**
 * @author Bonux
**/
public class PlayableBaseStats extends CreatureBaseStats
{
	public PlayableBaseStats(Playable owner)
	{
		super(owner);
	}

	@Override
	public Playable getOwner()
	{
		return (Playable) _owner;
	}

	@Override
	public double getPAtkSpd()
	{
		return getOwner().calcStat(Stats.BASE_P_ATK_SPD, super.getPAtkSpd(), null, null);
	}
}