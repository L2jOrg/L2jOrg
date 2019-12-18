package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class DamageShield extends AbstractStatAddEffect {
	public DamageShield(StatsSet params)
	{
		super(params, Stat.REFLECT_DAMAGE_PERCENT);
	}
}
