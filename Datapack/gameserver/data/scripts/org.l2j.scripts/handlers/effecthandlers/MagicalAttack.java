package handlers.effecthandlers;

import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Formulas;

import static org.l2j.gameserver.util.GameUtils.isAttackable;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Magical Attack effect implementation.
 * @author Adry_85
 */
public final class MagicalAttack extends AbstractEffect {
	private final double power;
	private final boolean overHit;
	private final double debuffModifier;
	
	public MagicalAttack(StatsSet params) {
		power = params.getDouble("power", 0);
		overHit = params.getBoolean("overHit", false);
		debuffModifier = params.getDouble("debuffModifier", 1);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.MAGICAL_ATTACK;
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
		
		if (isPlayer(effected) && effected.getActingPlayer().isFakeDeath()) {
			effected.stopFakeDeath(true);
		}
		
		if (overHit && isAttackable(effected)) {
			((Attackable) effected).overhitEnabled(true);
		}
		
		final boolean sps = skill.useSpiritShot() && effector.isChargedShot(ShotType.SPIRITSHOTS);
		final boolean bss = skill.useSpiritShot() && effector.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
		double damage = Formulas.calcMagicDam(effector, effected, skill, effector.getMAtk(), power, effected.getMDef(), sps, bss, mcrit);
		
		// Apply debuff mod
		if (effected.getEffectList().getDebuffCount() > 0) {
			damage *= debuffModifier;
		}
		
		effector.doAttack(damage, effected, skill, false, false, mcrit, false);
	}
}