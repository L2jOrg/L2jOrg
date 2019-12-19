package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class WeightLimit extends AbstractStatEffect {
	public WeightLimit(StatsSet params)
	{
		super(params, Stat.WEIGHT_LIMIT);
	}
}
