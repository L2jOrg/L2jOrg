package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class PhysicalAttackRange extends AbstractStatEffect {

	public PhysicalAttackRange(StatsSet params)
	{
		super(params, Stat.PHYSICAL_ATTACK_RANGE);
	}
}
