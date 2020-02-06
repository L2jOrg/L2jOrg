package handlers.effecthandlers;

import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Formulas;

import static org.l2j.gameserver.util.GameUtils.isAttackable;

/**
 * Backstab effect implementation.
 * @author Adry_85
 */
public final class Backstab extends AbstractEffect {

	public final double power;
	public final double chanceBoost;
	public final double criticalChance;
	public final boolean overHit;
	
	public Backstab(StatsSet params) {
		power = params.getDouble("power");
		chanceBoost = params.getDouble("chanceBoost");
		criticalChance = params.getDouble("criticalChance", 0);
		overHit = params.getBoolean("overHit", false);
	}
	
	@Override
	public boolean calcSuccess(Creature effector, Creature effected, Skill skill) {
		return !effector.isInFrontOf(effected) && !Formulas.calcPhysicalSkillEvasion(effector, effected, skill) && Formulas.calcBlowSuccess(effector, effected, skill, chanceBoost);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.PHYSICAL_ATTACK;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (effector.isAlikeDead()) {
			return;
		}
		
		if (overHit && isAttackable(effected)) {
			((Attackable) effected).overhitEnabled(true);
		}
		
		final boolean ss = skill.useSoulShot() && (effector.isChargedShot(ShotType.SOULSHOTS) || effector.isChargedShot(ShotType.BLESSED_SOULSHOTS));
		final byte shld = Formulas.calcShldUse(effector, effected);
		double damage = Formulas.calcBlowDamage(effector, effected, skill, true, power, shld, ss);
		
		if (Formulas.calcCrit(criticalChance, effector, effected, skill)) {
			damage *= 2;
		}
		
		effector.doAttack(damage, effected, skill, false, true, true, false);
	}
}