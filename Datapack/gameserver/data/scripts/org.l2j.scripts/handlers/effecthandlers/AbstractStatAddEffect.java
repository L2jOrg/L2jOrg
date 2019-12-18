package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class AbstractStatAddEffect extends AbstractEffect {

	private final Stat stat;
	protected final double amount;
	
	public AbstractStatAddEffect(StatsSet params, Stat stat) {
		this.stat = stat;
		amount = params.getDouble("amount", 0);
	}
	
	@Override
	public void pump(Creature effected, Skill skill)
	{
		effected.getStats().mergeAdd(stat, amount);
	}
}
