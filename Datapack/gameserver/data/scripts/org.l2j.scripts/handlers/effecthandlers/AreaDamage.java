package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class AreaDamage extends AbstractStatEffect {
	public AreaDamage(StatsSet params)
	{
		super(params, Stat.DAMAGE_ZONE_VULN);
	}
}
