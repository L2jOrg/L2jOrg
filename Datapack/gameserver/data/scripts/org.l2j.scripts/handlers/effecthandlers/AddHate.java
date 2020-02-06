package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isAttackable;

/**
 * Add Hate effect implementation.
 * @author Adry_85
 */
public final class AddHate extends AbstractEffect {

	public final double power;
	public final boolean affectSummoner;
	
	public AddHate(StatsSet params) {
		power = params.getDouble("power", 0);
		affectSummoner = params.getBoolean("affectSummoner", false);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (affectSummoner && nonNull(effector.getSummoner())) {
			effector = effector.getSummoner();
		}
		
		if (!isAttackable(effected)) {
			return;
		}
		
		final double val = power;
		if (val > 0) {
			((Attackable) effected).addDamageHate(effector, 0, (int) val);
		} else if (val < 0) {
			((Attackable) effected).reduceHate(effector, (int) -val);
		}
	}
}
