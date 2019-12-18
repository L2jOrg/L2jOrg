package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class ShieldDefence extends AbstractStatEffect {
	public ShieldDefence(StatsSet params)
	{
		super(params, Stat.SHIELD_DEFENCE);
	}
}
