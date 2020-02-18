package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.SkillCaster;

import static java.util.Objects.nonNull;

/**
 * Call Skill effect implementation.
 * @author NosBit
 * @author JoeAlisson
 */
public final class CallSkill extends AbstractEffect {

	private final SkillHolder skill;
	
	private CallSkill(StatsSet params) {
		skill = new SkillHolder(params.getInt("skill"), params.getInt("power", 1));
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		Skill triggerSkill = null;
		// Mobius: Use 0 to trigger max effector learned skill level.
		if (this.skill.getLevel() == 0) {
			final int knownLevel = effector.getSkillLevel(this.skill.getSkillId());

			if (knownLevel > 0) {
				triggerSkill = SkillEngine.getInstance().getSkill(this.skill.getSkillId(), knownLevel);
			} else {
				LOGGER.warn("Player {} called unknown skill {} triggered by {} CallSkill.", effector, this.skill, skill);
			}
		} else {
			triggerSkill = this.skill.getSkill();
		}
		
		if (nonNull(triggerSkill)) {
			SkillCaster.triggerCast(effector, effected, triggerSkill);
		} else {
			LOGGER.warn("Skill not found effect called from {}", skill);
		}
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new CallSkill(data);
		}

		@Override
		public String effectName() {
			return "call-skill";
		}
	}
}
