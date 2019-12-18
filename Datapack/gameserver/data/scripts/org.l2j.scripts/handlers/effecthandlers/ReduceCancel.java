package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class ReduceCancel extends AbstractStatEffect {
	public ReduceCancel(StatsSet params)
	{
		super(params, Stat.ATTACK_CANCEL);
	}
}
