package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class PvePhysicalAttackDefenceBonus extends AbstractStatEffect {
	public PvePhysicalAttackDefenceBonus(StatsSet params)
	{
		super(params, Stat.PVE_PHYSICAL_ATTACK_DEFENCE);
	}
}
