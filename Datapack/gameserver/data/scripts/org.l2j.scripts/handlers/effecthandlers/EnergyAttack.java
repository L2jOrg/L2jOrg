/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isAttackable;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Energy Attack effect implementation.
 * @author NosBit
 * @author JoeAlisson
 */
public final class EnergyAttack extends AbstractEffect {

	private final double power;
	private final int chargeConsume;
	private final int criticalChance;
	private final boolean ignoreShieldDefence;
	private final boolean overHit;
	private final double pDefMod;
	
	private EnergyAttack(StatsSet params) {
		power = params.getDouble("power", 0);
		criticalChance = params.getInt("critical-chance", 0);
		ignoreShieldDefence = params.getBoolean("ignore-shield", false);
		overHit = params.getBoolean("over-hit", false);
		chargeConsume = params.getInt("consume-charges", 0);
		pDefMod = params.getDouble("physical-defense-mod", 1.0);
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
			final double generalTraitMod = Formulas.calcGeneralTraitBonus(attacker, effected, skill.getTrait(), true);
			final double weaknessMod = Formulas.calcWeaknessBonus(attacker, effected, skill.getTrait());
			final double attributeMod = Formulas.calcAttributeBonus(attacker, effected, skill);
			final double pvpPveMod = Formulas.calculatePvpPveBonus(attacker, effected, skill, true);
			
			// Skill specific mods.
			final double energyChargesBoost = 1 + (charge * 0.1); // 10% bonus damage for each charge used.
			final double critMod = critical ? Formulas.calcCritDamage(attacker, effected, skill) : 1;
			double ssmod = skill.useSoulShot() ? attacker.chargedShotBonus(ShotType.SOULSHOTS) : 1;
			
			// ...................________Initial Damage_________...__Charges Additional Damage__...____________________________________
			// ATTACK CALCULATION ((77 * ((pAtk * lvlMod) + power) * (1 + (0.1 * chargesConsumed)) / pdef) * skillPower) + skillPowerAdd
			// ```````````````````^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^```^^^^^^^^^^^^^^^^^^^^^^^^^^^^^```^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
			final double baseMod = (77 * ((attacker.getPAtk() * attacker.getLevelMod()) + effector.getStats().getValue(Stat.PHYSICAL_SKILL_POWER, power))) / defence;
			damage = baseMod * ssmod * critMod * weaponTraitMod * generalTraitMod * weaknessMod * attributeMod * energyChargesBoost * pvpPveMod;
		}
		
		effector.doAttack(damage, effected, skill, false, false, critical, false);
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new EnergyAttack(data);
		}

		@Override
		public String effectName() {
			return "energy-attack";
		}
	}
}