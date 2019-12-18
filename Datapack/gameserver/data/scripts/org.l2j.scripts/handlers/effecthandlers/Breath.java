package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class Breath extends AbstractStatEffect {
	public Breath(StatsSet params)
	{
		super(params, Stat.BREATH);
	}
}
