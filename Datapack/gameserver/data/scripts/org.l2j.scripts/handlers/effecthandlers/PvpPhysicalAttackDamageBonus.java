package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class PvpPhysicalAttackDamageBonus extends AbstractStatEffect {
	public PvpPhysicalAttackDamageBonus(StatsSet params)
	{
		super(params, Stat.PVP_PHYSICAL_ATTACK_DAMAGE);
	}
}
