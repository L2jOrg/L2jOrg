package handlers.effecthandlers;

import org.l2j.gameserver.enums.SpeedType;
import org.l2j.gameserver.enums.StatModifierType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stat;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public final class Speed extends AbstractEffect {

	private final double amount;
	private final StatModifierType mode;
	private List<SpeedType> speedType;
	
	public Speed(StatsSet params) {
		amount = params.getDouble("amount", 0);
		mode = params.getEnum("mode", StatModifierType.class, StatModifierType.DIFF);
		speedType = params.getEnumList("weaponType", SpeedType.class);

		if (isNull(speedType)) {
			speedType = Collections.singletonList(SpeedType.ALL);
		}
	}
	
	@Override
	public void pump(Creature effected, Skill skill) {
		switch (mode) {
			case DIFF -> {
				for (SpeedType type : speedType) {
					switch (type) {
						case RUN -> effected.getStats().mergeAdd(Stat.RUN_SPEED, amount);
						case WALK -> effected.getStats().mergeAdd(Stat.WALK_SPEED, amount);
						case SWIM_RUN -> effected.getStats().mergeAdd(Stat.SWIM_RUN_SPEED, amount);
						case SWIM_WALK -> effected.getStats().mergeAdd(Stat.SWIM_WALK_SPEED, amount);
						case FLY_RUN -> effected.getStats().mergeAdd(Stat.FLY_RUN_SPEED, amount);
						case FLY_WALK -> effected.getStats().mergeAdd(Stat.FLY_WALK_SPEED, amount);
						default -> {
							effected.getStats().mergeAdd(Stat.RUN_SPEED, amount);
							effected.getStats().mergeAdd(Stat.WALK_SPEED, amount);
							effected.getStats().mergeAdd(Stat.SWIM_RUN_SPEED, amount);
							effected.getStats().mergeAdd(Stat.SWIM_WALK_SPEED, amount);
							effected.getStats().mergeAdd(Stat.FLY_RUN_SPEED, amount);
							effected.getStats().mergeAdd(Stat.FLY_WALK_SPEED, amount);
						}
					}
				}
			}
			case PER -> {
				for (SpeedType type : speedType) {
					switch (type) {
						case RUN -> effected.getStats().mergeMul(Stat.RUN_SPEED, (amount / 100) + 1);
						case WALK -> effected.getStats().mergeMul(Stat.WALK_SPEED, (amount / 100) + 1);
						case SWIM_RUN -> effected.getStats().mergeMul(Stat.SWIM_RUN_SPEED, (amount / 100) + 1);
						case SWIM_WALK -> effected.getStats().mergeMul(Stat.SWIM_WALK_SPEED, (amount / 100) + 1);
						case FLY_RUN -> effected.getStats().mergeMul(Stat.FLY_RUN_SPEED, (amount / 100) + 1);
						case FLY_WALK -> effected.getStats().mergeMul(Stat.FLY_WALK_SPEED, (amount / 100) + 1);
						default -> {
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
		}
	}
}
