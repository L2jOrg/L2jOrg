package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Mobius
 */
public class BonusDropRate extends AbstractStatEffect {
	public BonusDropRate(StatsSet params)
	{
		super(params, Stat.BONUS_DROP_RATE);
	}
}
