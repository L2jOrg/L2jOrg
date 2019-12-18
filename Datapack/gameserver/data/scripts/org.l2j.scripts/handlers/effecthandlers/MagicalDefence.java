package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class MagicalDefence extends AbstractStatEffect {
	public MagicalDefence(StatsSet params)
	{
		super(params, Stat.MAGICAL_DEFENCE);
	}
}
