package handlers.effecthandlers;

import handlers.effecthandlers.stat.AbstractStatEffect;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public final class Speed extends AbstractStatEffect {
	
	public Speed(StatsSet params) {
		super(params, Stat.RUN_SPEED);
	}
	
	@Override
	public void pump(Creature effected, Skill skill) {
		switch (mode) {
			case DIFF -> {
				effected.getStats().mergeAdd(Stat.RUN_SPEED, amount);
				effected.getStats().mergeAdd(Stat.WALK_SPEED, amount);
				effected.getStats().mergeAdd(Stat.SWIM_RUN_SPEED, amount);
				effected.getStats().mergeAdd(Stat.SWIM_WALK_SPEED, amount);
				effected.getStats().mergeAdd(Stat.FLY_RUN_SPEED, amount);
				effected.getStats().mergeAdd(Stat.FLY_WALK_SPEED, amount);
			}
			case PER -> {
				effected.getStats().mergeMul(Stat.RUN_SPEED, (amount / 100) + 1);
				effected.getStats().mergeMul(Stat.WALK_SPEED, (amount / 100) + 1);
				effected.getStats().mergeMul(Stat.SWIM_RUN_SPEED, (amount / 100) + 1);
				effected.getStats().mergeMul(Stat.SWIM_WALK_SPEED, (amount / 100) + 1);
				effected.getStats().mergeMul(Stat.FLY_RUN_SPEED, (amount / 100) + 1);
				effected.getStats().mergeMul(Stat.FLY_WALK_SPEED, (amount / 100) + 1);
			}
		}
	}
}
