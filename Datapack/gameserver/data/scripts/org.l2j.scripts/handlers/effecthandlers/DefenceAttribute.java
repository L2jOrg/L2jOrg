package handlers.effecthandlers;

import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stat;

import java.util.Collections;

/**
 * @author Sdw
 */
public class DefenceAttribute extends AbstractEffect {
	public final AttributeType attribute;
	public final double amount;
	
	public DefenceAttribute(StatsSet params) {
		amount = params.getDouble("amount", 0);
		attribute = params.getEnum("attribute", AttributeType.class, AttributeType.FIRE);
	}
	
	@Override
	public void pump(Creature effected, Skill skill) {
		effected.getStats().mergeAdd(attribute.toStatResist(), amount);
	}
}
