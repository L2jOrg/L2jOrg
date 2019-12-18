package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class MpRegen extends AbstractStatEffect {
	public MpRegen(StatsSet params)
	{
		super(params, Stat.REGENERATE_MP_RATE);
	}
}
