package handlers.effecthandlers;

import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class AttackAttribute extends AbstractEffect {

	private final AttributeType attribute;
	private final double amount;
	
	public AttackAttribute(StatsSet params) {
		amount = params.getDouble("amount", 0);
		attribute = params.getEnum("attribute", AttributeType.class, AttributeType.FIRE);
	}
	
	@Override
	public void pump(Creature effected, Skill skill) {
		Stat stat = switch (attribute) {
						case WATER -> Stat.WATER_POWER;
						case WIND ->  Stat.WIND_POWER;
						case EARTH -> Stat.EARTH_POWER;
						case HOLY -> Stat.HOLY_POWER;
						case DARK -> Stat.DARK_POWER;
						default ->   Stat.FIRE_POWER;
		};
		effected.getStats().mergeAdd(stat, amount);
	}
}
