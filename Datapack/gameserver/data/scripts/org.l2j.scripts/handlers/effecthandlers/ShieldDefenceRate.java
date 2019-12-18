package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class ShieldDefenceRate extends AbstractStatEffect {
	public ShieldDefenceRate(StatsSet params)
	{
		super(params, Stat.SHIELD_DEFENCE_RATE);
	}
}
