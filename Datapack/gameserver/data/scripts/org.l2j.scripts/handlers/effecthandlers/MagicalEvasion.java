package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class MagicalEvasion extends AbstractStatEffect {
	public MagicalEvasion(StatsSet params)
	{
		super(params, Stat.MAGIC_EVASION_RATE);
	}
}
