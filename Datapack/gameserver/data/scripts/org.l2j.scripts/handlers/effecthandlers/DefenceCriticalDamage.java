package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class DefenceCriticalDamage extends AbstractStatEffect {
	public DefenceCriticalDamage(StatsSet params)
	{
		super(params, Stat.DEFENCE_CRITICAL_DAMAGE, Stat.DEFENCE_CRITICAL_DAMAGE_ADD);
	}
}
