package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class LimitCp extends AbstractStatEffect {
	public LimitCp(StatsSet params)
	{
		super(params, Stat.MAX_RECOVERABLE_CP);
	}
}
