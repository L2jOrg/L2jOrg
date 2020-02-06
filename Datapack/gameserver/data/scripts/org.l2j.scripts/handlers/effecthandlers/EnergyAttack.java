package handlers.effecthandlers;

import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isAttackable;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Energy Attack effect implementation.
 * @author NosBit
 */
public final class EnergyAttack extends AbstractEffect {

	public final double power;
	public final int chargeConsume;
	public final int criticalChance;
	public final boolean ignoreShieldDefence;
	public final boolean overHit;
	public final double pDefMod;
	
	public EnergyAttack(StatsSet params) {
		power = params.getDouble("power", 0);
		criticalChance = params.getInt("criticalChance", 0);
		ignoreShieldDefence = params.getBoolean("ignoreShieldDefence", false);
		overHit = params.getBoolean("overHit", false);
		chargeConsume = params.getInt("chargeConsume", 0);
		pDefMod = params.getDouble("pDefMod", 1.0);
	}
	
	@Override
	public boolean calcSuccess(Creature effector, Creature effected, Skill skill) {
		// TODO: Verify this on retail
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
		
		final Player attacker = effector.getActingPlayer();
		
		final int charge = Math.min(chargeConsume, attacker.getCharges());
		
		if (!attacker.decreaseCharges(charge)) {
			attacker.sendPacket(getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skill));
			return;
		}
		
		if (overHit && isAttackable(effected)) {
			((Attackable) effected).overhitEnabled(true);
		}
		
		double defence = effected.getPDef() * pDefMod;
		
		if (!ignoreShieldDefence) {
			final byte shield = Formulas.calcShldUse(attacker, effected);
			switch (shield) {
				case Formulas.SHIELD_DEFENSE_SUCCEED -> defence += effected.getShldDef();
				case Formulas.SHIELD_DEFENSE_PERFECT_BLOCK -> defence = -1;
			}
		}
		
		double damage = 1;
		final boolean critical = Formulas.calcCrit(criticalChance, attacker, effected, skill);
		
		if (defence != -1) {
			// Trait, elements
			final double weaponTraitMod = Formulas.calcWeaponTraitBonus(attacker, effected);
			final double generalTraitMod = Formulas.calcGeneralTraitBonus(attacker, effected, skill.getTraitType(), true);
			final double weaknessMod = Formulas.calcWeaknessBonus(attacker, effected, skill.getTraitType());
			final double attributeMod = Formulas.calcAttributeBonus(attacker, effected, skill);
			final double pvpPveMod = Formulas.calculatePvpPveBonus(attacker, effected, skill, true);
			
			// Skill specific mods.
			final double energyChargesBoost = 1 + (charge * 0.1); // 10% bonus damage for each charge used.
			final double critMod = critical ? Formulas.calcCritDamage(attacker, effected, skill) : 1;
			double ssmod = 1;

			if (skill.useSoulShot()) {
				if (attacker.isChargedShot(ShotType.SOULSHOTS)) {
					ssmod = 2 * attacker.getStats().getValue(Stat.SHOTS_BONUS); // 2.04 for dual weapon?
				} else if (attacker.isChargedShot(ShotType.BLESSED_SOULSHOTS)) {
					ssmod = 4 * attacker.getStats().getValue(Stat.SHOTS_BONUS);
				}
			}
			
			// ...................________Initial Damage_________...__Charges Additional Damage__...____________________________________
			// ATTACK CALCULATION ((77 * ((pAtk * lvlMod) + power) * (1 + (0.1 * chargesConsumed)) / pdef) * skillPower) + skillPowerAdd
			// ```````````````````^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^```^^^^^^^^^^^^^^^^^^^^^^^^^^^^^```^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
			final double baseMod = (77 * ((attacker.getPAtk() * attacker.getLevelMod()) + power + effector.getStats().getValue(Stat.SKILL_POWER_ADD, 0))) / defence;
			damage = baseMod * ssmod * critMod * weaponTraitMod * generalTraitMod * weaknessMod * attributeMod * energyChargesBoost * pvpPveMod;
		}

		damage = Math.max(0, damage * effector.getStats().getValue(Stat.PHYSICAL_SKILL_POWER, 1));
		
		effector.doAttack(damage, effected, skill, false, false, critical, false);
	}
}