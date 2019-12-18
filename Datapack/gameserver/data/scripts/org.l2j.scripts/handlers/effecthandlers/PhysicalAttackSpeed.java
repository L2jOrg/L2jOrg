package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class PhysicalAttackSpeed extends AbstractStatEffect {

	public PhysicalAttackSpeed(StatsSet params)
	{
		super(params, Stat.PHYSICAL_ATTACK_SPEED);
	}
}
