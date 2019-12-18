package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class Accuracy extends AbstractStatEffect {

	public Accuracy(StatsSet params)
	{
		super(params, Stat.ACCURACY_COMBAT);
	}
}
