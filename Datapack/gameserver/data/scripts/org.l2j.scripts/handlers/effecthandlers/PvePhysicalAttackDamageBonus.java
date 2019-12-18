package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class PvePhysicalAttackDamageBonus extends AbstractStatEffect {
	public PvePhysicalAttackDamageBonus(StatsSet params)
	{
		super(params, Stat.PVE_PHYSICAL_ATTACK_DAMAGE);
	}
}
