package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.BaseStats;

/**
 * @author Sdw
 */
public class StatUp extends AbstractEffect {
	public final BaseStats stat;
	public final double amount;
	
	public StatUp(StatsSet params) {
		amount = params.getDouble("amount", 0);
		stat = params.getEnum("stat", BaseStats.class, BaseStats.STR);
	}
	
	@Override
	public void pump(Creature effected, Skill skill) {
		effected.getStats().mergeAdd(stat.getStat(), amount);
	}
}
