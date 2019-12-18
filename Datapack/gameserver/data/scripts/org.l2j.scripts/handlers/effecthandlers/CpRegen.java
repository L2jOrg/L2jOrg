package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class CpRegen extends AbstractStatEffect {
	public CpRegen(StatsSet params)
	{
		super(params, Stat.REGENERATE_CP_RATE);
	}
}
