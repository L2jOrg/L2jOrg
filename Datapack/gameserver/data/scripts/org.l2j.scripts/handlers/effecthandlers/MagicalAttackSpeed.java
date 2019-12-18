package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class MagicalAttackSpeed extends AbstractStatEffect {

	public MagicalAttackSpeed(StatsSet params)
	{
		super(params, Stat.MAGIC_ATTACK_SPEED);
	}
}
