package handlers.effecthandlers;

import java.util.List;

import org.l2j.gameserver.enums.DispelSlotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Formulas;

/**
 * Dispel By Category effect implementation.
 * @author DS, Adry_85
 */
public final class DispelByCategory extends AbstractEffect {
	private final DispelSlotType slot;
	private final int rate;
	private final int max;
	
	public DispelByCategory(StatsSet params) {
		slot = params.getEnum("slot", DispelSlotType.class, DispelSlotType.BUFF);
		rate = params.getInt("rate", 0);
		max = params.getInt("max", 0);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.DISPEL;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (effected.isDead()) {
			return;
		}
		
		final List<BuffInfo> canceled = Formulas.calcCancelStealEffects(effector, effected, skill, slot, rate, max);
		canceled.forEach(b -> effected.getEffectList().stopSkillEffects(true, b.getSkill()));
	}
}