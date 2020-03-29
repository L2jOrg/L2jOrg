package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
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
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Soul Blow effect implementation.
 * @author Adry_85
 */
public final class SoulBlow extends AbstractEffect {
	private final double power;
	private final double chanceBoost;
	private final boolean overHit;
	
	private SoulBlow(StatsSet params) {
		power = params.getDouble("power");
		chanceBoost = params.getDouble("chance-boost");
		overHit = params.getBoolean("over-hit", false);
	}
	
	/**
	 * If is not evaded and blow lands.
	 * @param effector
	 * @param effected
	 * @param skill
	 */
	@Override
	public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
	{
		return !Formulas.calcPhysicalSkillEvasion(effector, effected, skill) && Formulas.calcBlowSuccess(effector, effected, skill, chanceBoost);
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

		double damage = Formulas.calcBlowDamage(effector, effected, skill, power);
		if (skill.getMaxSoulConsumeCount() > 0 && isPlayer(effector)) {
			// Souls Formula (each soul increase +4%)
			final int chargedSouls = Math.min(effector.getActingPlayer().getChargedSouls(), skill.getMaxSoulConsumeCount());
			damage *= 1 + (chargedSouls * 0.04);
		}
		
		effector.doAttack(damage, effected, skill, false, false, true, false);
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new SoulBlow(data);
		}

		@Override
		public String effectName() {
			return "soul-blow";
		}
	}
}