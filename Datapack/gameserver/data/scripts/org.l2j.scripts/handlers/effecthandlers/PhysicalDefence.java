package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class PhysicalDefence extends AbstractConditionalHpEffect {

	public PhysicalDefence(StatsSet params) {
		super(params, Stat.PHYSICAL_DEFENCE);
	}
}
