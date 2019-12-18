package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class MagicAccuracy extends AbstractStatEffect {
	public MagicAccuracy(StatsSet params)
	{
		super(params, Stat.ACCURACY_MAGIC);
	}
}
