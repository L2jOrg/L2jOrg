package handlers.effecthandlers;

import org.l2j.gameserver.enums.DispelSlotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class ResistAbnormalByCategory extends AbstractEffect {
	public final DispelSlotType slot;
	public final double amount;
	
	public ResistAbnormalByCategory(StatsSet params) {
		amount = params.getDouble("amount", 0);
		slot = params.getEnum("slot", DispelSlotType.class, DispelSlotType.DEBUFF);
	}
	
	@Override
	public void pump(Creature effected, Skill skill) {
		// Only this one is in use it seems
		if (slot == DispelSlotType.DEBUFF) {
			effected.getStats().mergeMul(Stat.RESIST_ABNORMAL_DEBUFF, 1 + (amount / 100));
		}
	}
}
