package handlers.effecthandlers;

import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Formulas;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Magical Attack effect implementation.
 * @author Adry_85
 */
public final class MagicalAttackRange extends AbstractEffect {
	private final double power;
	private final double shieldDefPercent;
	
	public MagicalAttackRange(StatsSet params) {
		power = params.getDouble("power");
		shieldDefPercent = params.getDouble("shieldDefPercent", 0);
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
		if (isPlayer(effected) && effected.getActingPlayer().isFakeDeath()) {
			effected.stopFakeDeath(true);
		}
		
		double mDef = effected.getMDef();
		switch (Formulas.calcShldUse(effector, effected)) {
			case Formulas.SHIELD_DEFENSE_SUCCEED -> mDef += ((effected.getShldDef() * shieldDefPercent) / 100);
			case Formulas.SHIELD_DEFENSE_PERFECT_BLOCK -> mDef = -1;
		}
		
		double damage = 1;
		final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
		
		if (mDef != -1) {
			final boolean sps = skill.useSpiritShot() && effector.isChargedShot(ShotType.SPIRITSHOTS);
			final boolean bss = skill.useSpiritShot() && effector.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
			
			damage = Formulas.calcMagicDam(effector, effected, skill, effector.getMAtk(), power, mDef, sps, bss, mcrit);
		}
		
		effector.doAttack(damage, effected, skill, false, false, mcrit, false);
	}
}