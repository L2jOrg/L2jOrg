package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Mobius
 */
public class AgathionSlot extends AbstractStatAddEffect {
	public AgathionSlot(StatsSet params)
	{
		super(params, Stat.AGATHION_SLOTS);
	}
}
