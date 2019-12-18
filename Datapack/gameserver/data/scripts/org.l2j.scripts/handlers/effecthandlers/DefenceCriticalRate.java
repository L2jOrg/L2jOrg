package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class DefenceCriticalRate extends AbstractStatEffect {
	public DefenceCriticalRate(StatsSet params)
	{
		super(params, Stat.DEFENCE_CRITICAL_RATE, Stat.DEFENCE_CRITICAL_RATE_ADD);
	}
}
