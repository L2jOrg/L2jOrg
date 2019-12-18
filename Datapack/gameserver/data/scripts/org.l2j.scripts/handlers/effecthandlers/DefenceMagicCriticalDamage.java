package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class DefenceMagicCriticalDamage extends AbstractStatEffect {
	public DefenceMagicCriticalDamage(StatsSet params)
	{
		super(params, Stat.DEFENCE_MAGIC_CRITICAL_DAMAGE);
	}
}
