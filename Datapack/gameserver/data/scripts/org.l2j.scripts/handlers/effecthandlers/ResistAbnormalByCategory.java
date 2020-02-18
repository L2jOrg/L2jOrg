package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.DispelSlotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class ResistAbnormalByCategory extends AbstractEffect {
	private final DispelSlotType category;
	private final double power;
	
	private ResistAbnormalByCategory(StatsSet params) {
		power = params.getDouble("power", 0);
		category = params.getEnum("category", DispelSlotType.class, DispelSlotType.DEBUFF);
	}
	
	@Override
	public void pump(Creature effected, Skill skill) {
		// Only this one is in use it seems
		if (category == DispelSlotType.DEBUFF) {
			effected.getStats().mergeMul(Stat.RESIST_ABNORMAL_DEBUFF, 1 + (power / 100));
		}
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new ResistAbnormalByCategory(data);
		}

		@Override
		public String effectName() {
			return "resist-abnormal-by-category";
		}
	}
}
