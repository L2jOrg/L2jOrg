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
 */
public final class SkillTurning extends AbstractEffect {
	public final int chance;
	private final boolean staticChance;
	
	public SkillTurning(StatsSet params) {
		chance = params.getInt("chance", 100);
		staticChance = params.getBoolean("staticChance", false);
	}
	
	@Override
	public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
	{
		return staticChance ? Formulas.calcProbability(chance, effector, effected, skill) : (Rnd.get(100) < chance);
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