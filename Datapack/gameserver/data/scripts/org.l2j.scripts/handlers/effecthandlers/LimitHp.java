package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class LimitHp extends AbstractStatEffect {
	public LimitHp(StatsSet params)
	{
		super(params, Stat.MAX_RECOVERABLE_HP);
	}
}
