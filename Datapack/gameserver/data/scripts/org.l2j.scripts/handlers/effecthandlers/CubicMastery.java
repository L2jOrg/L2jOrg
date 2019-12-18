package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public final class CubicMastery extends AbstractStatAddEffect {
	public CubicMastery(StatsSet params)
	{
		super(params, Stat.MAX_CUBIC);
	}
}
