package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class DefenceMagicCriticalRate extends AbstractStatEffect {
	public DefenceMagicCriticalRate(StatsSet params)
	{
		super(params, Stat.DEFENCE_MAGIC_CRITICAL_RATE, Stat.DEFENCE_MAGIC_CRITICAL_RATE_ADD);
	}
}
