package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.DispelSlotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.skills.EffectScope;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Formulas;

import java.util.List;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Steal Abnormal effect implementation.
 * @author Adry_85, Zoey76
 */
public final class StealAbnormal extends AbstractEffect {

	private final DispelSlotType slot;
	private final int rate;
	private final int power;
	
	private StealAbnormal(StatsSet params) {
		slot = params.getEnum("category", DispelSlotType.class, DispelSlotType.BUFF);
		rate = params.getInt("rate", 0);
		power = params.getInt("power", 0);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.STEAL_ABNORMAL;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (isPlayer(effected) && effector != effected) {
			final List<BuffInfo> toSteal = Formulas.calcCancelStealEffects(effector, effected, skill, slot, rate, power);
			if (toSteal.isEmpty()) {
				return;
			}
			
			for (BuffInfo infoToSteal : toSteal) {
				// Invert effected and effector.
				final BuffInfo stolen = new BuffInfo(effected, effector, infoToSteal.getSkill(), false, null, null);
				stolen.setAbnormalTime(infoToSteal.getTime()); // Copy the remaining time.
				// To include all the effects, it's required to go through the template rather the buff info.
				infoToSteal.getSkill().applyEffectScope(EffectScope.GENERAL, stolen, true, true);
				effected.getEffectList().remove(infoToSteal, true, true, true);
				effector.getEffectList().add(stolen);
			}
		}
	}
	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new StealAbnormal(data);
		}

		@Override
		public String effectName() {
			return "steal-abnormal";
		}
	}
}