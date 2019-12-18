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
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isAttackable;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Physical Soul Attack effect implementation.<br>
 * <b>Note</b>: Initial formula taken from PhysicalAttack.
 * @author Adry_85, Nik
 */
public final class PhysicalSoulAttack extends AbstractEffect {

	private final double power;
	private final double criticalChance;
	private final boolean ignoreShieldDefence;
	private final boolean overHit;
	
	public PhysicalSoulAttack(StatsSet params) {
		power = params.getDouble("power", 0);
		criticalChance = params.getDouble("criticalChance", 0);
		ignoreShieldDefence = params.getBoolean("ignoreShieldDefence", false);
		overHit = params.getBoolean("overHit", false);
	}
	
	@Override
	public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
	{
		return !Formulas.calcPhysicalSkillEvasion(effector, effected, skill);
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
		if (!isPlayer(effector)) {
			return;
		}
		
		if (effector.isAlikeDead()) {
			return;
		}
		
		if (isPlayer(effected) && effected.getActingPlayer().isFakeDeath()) {
			effected.stopFakeDeath(true);
		}
		
		final int souls = Math.min(skill.getMaxSoulConsumeCount(), effector.getActingPlayer().getCharges());

		if (!effector.getActingPlayer().decreaseCharges(souls)) {
			effector.sendPacket(getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skill));
			return;
		}
		
		if (overHit && isAttackable(effected)) {
			((Attackable) effected).overhitEnabled(true);
		}
		
		final double attack = effector.getPAtk();
		double defence = effected.getPDef();
		
		if (!ignoreShieldDefence) {
			switch (Formulas.calcShldUse(effector, effected)) {
				case Formulas.SHIELD_DEFENSE_SUCCEED -> defence += effected.getShldDef();
				case Formulas.SHIELD_DEFENSE_PERFECT_BLOCK -> defence = -1;
			}
		}
		
		double damage = 1;
		final boolean critical = Formulas.calcCrit(criticalChance, effector, effected, skill);
		
		if (defence != -1) {
			// Trait, elements
			final double weaponTraitMod = Formulas.calcWeaponTraitBonus(effector, effected);
			final double generalTraitMod = Formulas.calcGeneralTraitBonus(effector, effected, skill.getTraitType(), true);
			final double weaknessMod = Formulas.calcWeaknessBonus(effector, effected, skill.getTraitType());
			final double attributeMod = Formulas.calcAttributeBonus(effector, effected, skill);
			final double pvpPveMod = Formulas.calculatePvpPveBonus(effector, effected, skill, true);
			final double randomMod = effector.getRandomDamageMultiplier();
			
			// Skill specific mods.
			final double weaponMod = effector.getAttackType().isRanged() ? 70 : 77;
			final double power = this.power + effector.getStats().getValue(Stat.SKILL_POWER_ADD, 0);
			final double rangedBonus = effector.getAttackType().isRanged() ? attack + power : 0;
			final double critMod = critical ? Formulas.calcCritDamage(effector, effected, skill) : 1;
			double ssmod = 1;

			if (skill.useSoulShot()) {
				if (effector.isChargedShot(ShotType.SOULSHOTS)) {
					ssmod = 2 * effector.getStats().getValue(Stat.SHOTS_BONUS); // 2.04 for dual weapon?
				} else if (effector.isChargedShot(ShotType.BLESSED_SOULSHOTS)) {
					ssmod = 4 * effector.getStats().getValue(Stat.SHOTS_BONUS);
				}
			}
			final double soulsMod = 1 + (souls * 0.04); // Souls Formula (each soul increase +4%)
			
			// ...................____________Melee Damage_____________......................................___________________Ranged Damage____________________
			// ATTACK CALCULATION 77 * ((pAtk * lvlMod) + power) / pdef            RANGED ATTACK CALCULATION 70 * ((pAtk * lvlMod) + power + patk + power) / pdef
			// ```````````````````^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^``````````````````````````````````````^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
			final double baseMod = (weaponMod * ((attack * effector.getLevelMod()) + power + rangedBonus)) / defence;
			damage = baseMod * soulsMod * ssmod * critMod * weaponTraitMod * generalTraitMod * weaknessMod * attributeMod * pvpPveMod * randomMod;
			damage *= effector.getStats().getValue(Stat.PHYSICAL_SKILL_POWER, 1);
		}
		
		effector.doAttack(damage, effected, skill, false, false, critical, false);
	}
}
