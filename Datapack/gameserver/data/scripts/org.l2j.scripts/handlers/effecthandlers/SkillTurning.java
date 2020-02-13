package handlers.effecthandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Formulas;

/**
 * Skill Turning effect implementation.
 * @author JoeAlisson
 */
public final class SkillTurning extends AbstractEffect {
	public final int power;
	
	public SkillTurning(StatsSet params) {
		power = params.getInt("power", 100);
	}
	
	@Override
	public boolean calcSuccess(Creature effector, Creature effected, Skill skill) {
		return Rnd.chance(power);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (effected == effector || effected.isRaid()) {
			return;
		}
		
		effected.breakCast();
	}
}