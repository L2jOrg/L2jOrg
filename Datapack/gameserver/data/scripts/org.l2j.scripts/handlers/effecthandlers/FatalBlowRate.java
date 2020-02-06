package handlers.effecthandlers;

import org.l2j.gameserver.enums.StatModifierType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Mobius
 * @author JoeAlisson
 */
public class FatalBlowRate extends AbstractEffect {
	public final double amount;
	public final StatModifierType mode;
	
	public FatalBlowRate(StatsSet params) {
		amount = params.getDouble("amount", 0);
		mode = params.getEnum("mode", StatModifierType.class, StatModifierType.DIFF);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
		switch (mode) {
			case DIFF -> effector.getStats().mergeAdd(Stat.BLOW_RATE, amount);
			case PER -> effector.getStats().mergeMul(Stat.BLOW_RATE, (amount / 100) + 1);
		}
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill) {
		switch (mode) {
			case DIFF -> effector.getStats().mergeAdd(Stat.BLOW_RATE, amount * -1);
			case PER -> effector.getStats().mergeMul(Stat.BLOW_RATE, ((amount / 100) + 1) * -1);
		}
	}
}
