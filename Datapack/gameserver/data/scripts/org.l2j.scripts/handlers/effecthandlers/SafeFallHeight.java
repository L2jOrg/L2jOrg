package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class SafeFallHeight extends AbstractStatEffect {
	public SafeFallHeight(StatsSet params)
	{
		super(params, Stat.FALL);
	}
}
