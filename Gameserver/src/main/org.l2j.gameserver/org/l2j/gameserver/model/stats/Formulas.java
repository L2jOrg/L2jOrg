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
package org.l2j.gameserver.model.stats;

import org.l2j.commons.util.CommonUtil;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.data.xml.impl.HitConditionBonusData;
import org.l2j.gameserver.data.xml.impl.KarmaData;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillType;
import org.l2j.gameserver.enums.*;
import org.l2j.gameserver.model.DamageInfo.DamageType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.SiegeFlag;
import org.l2j.gameserver.model.actor.instance.StaticWorldObject;
import org.l2j.gameserver.model.cubic.CubicInstance;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.item.Armor;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.type.ArmorType;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.*;
import static org.l2j.gameserver.util.MathUtil.convertHeadingToDegree;

/**
 * Global calculations.
 */
public final class Formulas {
    public static final byte SHIELD_DEFENSE_FAILED = 0; // no shield defense
    public static final byte SHIELD_DEFENSE_SUCCEED = 1; // normal shield defense
    public static final byte SHIELD_DEFENSE_PERFECT_BLOCK = 2; // perfect block
    public static final int SKILL_LAUNCH_TIME = 500; // The time to pass after the skill launching until the skill to affect targets. In milliseconds
    /**
     * Regeneration Task period.
     */
    private static final int HP_REGENERATE_PERIOD = 3000; // 3 secsEa
    private static final byte MELEE_ATTACK_RANGE = 40;

    /**
     * Return the period between 2 regeneration task (3s for Creature, 5 min for Door).
     *
     * @param cha
     * @return
     */
    public static int getRegeneratePeriod(Creature cha) {
        return isDoor(cha) ? HP_REGENERATE_PERIOD * 100 : HP_REGENERATE_PERIOD;
    }

    public static double calcBlowDamage(Creature attacker, Creature target, Skill skill, double power) {
        double defence = target.getPDef();

        switch (calcShldUse(attacker, target)) {
            case SHIELD_DEFENSE_SUCCEED -> defence += target.getShldDef();
            case SHIELD_DEFENSE_PERFECT_BLOCK -> {
                return 1;
            }
        }

        // Critical
        final var position = Position.getPosition(attacker, target);
        final var criticalMod = attacker.getStats().getValue(Stat.CRITICAL_DAMAGE, 1);
        final var criticalPositionMod = attacker.getStats().getPositionTypeValue(Stat.CRITICAL_DAMAGE, position);
        final var criticalVulnMod = target.getStats().getValue(Stat.DEFENCE_CRITICAL_DAMAGE, 1);
        final var criticalAddMod = attacker.getStats().getValue(Stat.CRITICAL_DAMAGE_ADD, 0);
        final var criticalAddVuln = target.getStats().getValue(Stat.DEFENCE_CRITICAL_DAMAGE_ADD, 0);

        // Trait, elements
        final var weaponTraitMod = calcWeaponTraitBonus(attacker, target);
        final var generalTraitMod = calcGeneralTraitBonus(attacker, target, skill.getTrait(), true);
        final var weaknessMod = calcWeaknessBonus(attacker, target, skill.getTrait());
        final var attributeMod = calcAttributeBonus(attacker, target, skill);
        final var randomMod = attacker.getRandomDamageMultiplier();
        final var pvpPveMod = calculatePvpPveBonus(attacker, target, skill, true);

        // Initial damage
        final var ssmod = attacker.chargedShotBonus(ShotType.SOULSHOTS); // + 0.04 for dual weapon?
        final var cdMult = criticalMod * (((criticalPositionMod - 1) / 2) + 1) * (((criticalVulnMod - 1) / 2) + 1);
        final var cdPatk = criticalAddMod + criticalAddVuln;
        final var positionMod = position == Position.BACK ? 0.2 : position == Position.SIDE ? 0.05 : 0;

        // ........................_____________________________Initial Damage____________________________...___________Position Additional Damage___________..._CriticalAdd_
        // ATTACK CALCULATION 77 * [(skillpower+patk) * 0.666 * cdbonus * cdPosBonusHalf * cdVulnHalf * ss + isBack0.2Side0.05 * (skillpower+patk*ss) * random + 6 * cd_patk] / pdef
        // ````````````````````````^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^```^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^```^^^^^^^^^^^^
        final var baseMod = (77 * (((power + attacker.getPAtk()) * 0.666) + (positionMod * (power + attacker.getPAtk()) * randomMod) + (6 * cdPatk))) / defence;
        return baseMod * ssmod * cdMult * weaponTraitMod * generalTraitMod * weaknessMod * attributeMod * randomMod * pvpPveMod;
    }

    public static double calcMagicDam(Creature attacker, Creature target, Skill skill, double mAtk, double power, double mDef, boolean mcrit) {

        var shotsBonus = skill.useSpiritShot() ? attacker.chargedShotBonus(ShotType.SPIRITSHOTS) : 1;
        final double critMod = mcrit ? calcCritDamage(attacker, target, skill) : 1; // TODO not really a proper way... find how it works then implement. // damage += attacker.getStat().getValue(Stats.MAGIC_CRIT_DMG_ADD, 0);

        // Trait, elements
        final double generalTraitMod = calcGeneralTraitBonus(attacker, target, skill.getTrait(), true);
        final double weaknessMod = calcWeaknessBonus(attacker, target, skill.getTrait());
        final double attributeMod = calcAttributeBonus(attacker, target, skill);
        final double randomMod = attacker.getRandomDamageMultiplier();
        final double pvpPveMod = calculatePvpPveBonus(attacker, target, skill, mcrit);

        // MDAM Formula.
        double damage = ( (77 *  attacker.getStats().getValue(Stat.MAGICAL_SKILL_POWER, power)  * Math.sqrt(mAtk) ) / mDef) * shotsBonus;

        // Failure calculation
        if (Config.ALT_GAME_MAGICFAILURES && !calcMagicSuccess(attacker, target, skill)) {
            if (isPlayer(attacker)) {
                if (calcMagicSuccess(attacker, target, skill)) {
                    if (skill.hasAnyEffectType(EffectType.HP_DRAIN)) {
                        attacker.sendPacket(SystemMessageId.DRAIN_WAS_ONLY_50_SUCCESSFUL);
                    } else {
                        attacker.sendPacket(SystemMessageId.YOUR_ATTACK_HAS_FAILED);
                    }
                    damage /= 2;
                } else {
                    final SystemMessage sm = getSystemMessage(SystemMessageId.C1_HAS_RESISTED_YOUR_S2);
                    sm.addString(target.getName());
                    sm.addSkillName(skill);
                    attacker.sendPacket(sm);
                    damage = 1;
                }
            }

            if (isPlayer(target)) {
                final SystemMessage sm = (skill.hasAnyEffectType(EffectType.HP_DRAIN)) ? getSystemMessage(SystemMessageId.YOU_RESISTED_C1_S_DRAIN) : getSystemMessage(SystemMessageId.YOU_RESISTED_C1_S_MAGIC);
                sm.addString(attacker.getName());
                target.sendPacket(sm);
            }
        }

        return damage * critMod * generalTraitMod * weaknessMod * attributeMod * randomMod * pvpPveMod;
    }

    /**
     * Returns true in case of critical hit
     *
     * @param rate
     * @param skill
     * @param activeChar
     * @param target
     * @return
     */
    public static boolean calcCrit(double rate, Creature activeChar, Creature target, Skill skill) {
        // Skill critical rate is calculated up to the first decimal, thats why multiply by 10 and compare to 1000.
        if (skill != null) {
            // Magic Critical Rate
            if (skill.isMagic()) {
                rate += activeChar.getStats().getValue(Stat.MAGIC_CRITICAL_RATE);
                if ((target == null) || !skill.isBad()) {
                    return Math.min(rate, 320) > Rnd.get(1000);
                }

                double finalRate = target.getStats().getValue(Stat.DEFENCE_MAGIC_CRITICAL_RATE, rate) + target.getStats().getValue(Stat.DEFENCE_MAGIC_CRITICAL_RATE_ADD, 0);
                if ((activeChar.getLevel() >= 78) && (target.getLevel() >= 78)) {
                    finalRate += Math.sqrt(activeChar.getLevel()) + ((activeChar.getLevel() - target.getLevel()) / 25.);
                    return Math.min(finalRate, 320) > Rnd.get(1000);
                }

                return Math.min(finalRate, 200) > Rnd.get(1000);
            }

            // Physical skill critical rate
            final double statBonus;

            // There is a chance that activeChar has altered base stat for skill critical.
            byte skillCritRateStat = (byte) activeChar.getStats().getValue(Stat.STAT_BONUS_SKILL_CRITICAL);
            if ((skillCritRateStat >= 0) && (skillCritRateStat < BaseStats.values().length)) {
                // Best tested
                statBonus = BaseStats.values()[skillCritRateStat].calcBonus(activeChar);
            } else {
                // Default base stat used for skill critical formula is STR.
                statBonus = BaseStats.STR.calcBonus(activeChar);
            }

            final double rateBonus = activeChar.getStats().getValue(Stat.CRITICAL_RATE_SKILL, 1);
            double finalRate = rate * statBonus * rateBonus * 10;
            return finalRate > Rnd.get(1000);
        }

        // Autoattack critical rate.
        // Even though, visible critical rate is capped to 500, you can reach higher than 50% chance with position and level modifiers.
        // TODO: Find retail-like calculation for criticalRateMod.
        final double criticalRateMod = (target.getStats().getValue(Stat.DEFENCE_CRITICAL_RATE, rate) + target.getStats().getValue(Stat.DEFENCE_CRITICAL_RATE_ADD, 0)) / 10;
        final double criticalLocBonus = calcCriticalPositionBonus(activeChar, target);
        final double criticalHeightBonus = calcCriticalHeightBonus(activeChar, target);
        rate = criticalLocBonus * criticalRateMod * criticalHeightBonus;

        // Autoattack critical depends on level difference at high levels as well.
        if ((activeChar.getLevel() >= 78) || (target.getLevel() >= 78)) {
            rate += (Math.sqrt(activeChar.getLevel()) * (activeChar.getLevel() - target.getLevel()) * 0.125);
        }

        // Autoattack critical rate is limited between 3%-97%.
        rate = CommonUtil.constrain(rate, 3, 97);

        return rate > Rnd.get(100);
    }

    /**
     * Gets the default (10% for side, 30% for back) positional critical rate bonus and multiplies it by any buffs that give positional critical rate bonus.
     *
     * @param activeChar the attacker.
     * @param target     the target.
     * @return a multiplier representing the positional critical rate bonus. Autoattacks for example get this bonus on top of the already capped critical rate of 500.
     */
    public static double calcCriticalPositionBonus(Creature activeChar, Creature target) {
        // final Position position = activeChar.getStat().has(Stats.ATTACK_BEHIND) ? Position.BACK : Position.getPosition(activeChar, target);
        switch (Position.getPosition(activeChar, target)) {
            case SIDE: // 10% Critical Chance bonus when attacking from side.
            {
                return 1.1 * activeChar.getStats().getPositionTypeValue(Stat.CRITICAL_RATE, Position.SIDE);
            }
            case BACK: // 30% Critical Chance bonus when attacking from back.
            {
                return 1.3 * activeChar.getStats().getPositionTypeValue(Stat.CRITICAL_RATE, Position.BACK);
            }
            default: // No Critical Chance bonus when attacking from front.
            {
                return activeChar.getStats().getPositionTypeValue(Stat.CRITICAL_RATE, Position.FRONT);
            }
        }
    }

    public static double calcCriticalHeightBonus(ILocational from, ILocational target) {
        return ((((CommonUtil.constrain(from.getZ() - target.getZ(), -25, 25) * 4) / 5) + 10) / 100) + 1;
    }

    /**
     * @param attacker
     * @param target
     * @param skill    {@code skill} to be used in the calculation, else calculation will result for autoattack.
     * @return regular critical damage bonus. Positional bonus is excluded!
     */
    public static double calcCritDamage(Creature attacker, Creature target, Skill skill) {
        final double criticalDamage;
        final double defenceCriticalDamage;

        if (skill != null) {
            if (skill.isMagic()) {
                // Magic critical damage.
                criticalDamage = attacker.getStats().getValue(Stat.MAGIC_CRITICAL_DAMAGE, 1);
                defenceCriticalDamage = target.getStats().getValue(Stat.DEFENCE_MAGIC_CRITICAL_DAMAGE, 1);
            } else {
                criticalDamage = attacker.getStats().getValue(Stat.CRITICAL_DAMAGE_SKILL, 1);
                defenceCriticalDamage = target.getStats().getValue(Stat.DEFENCE_CRITICAL_DAMAGE_SKILL, 1);
            }
        } else {
            // Autoattack critical damage.
            criticalDamage = attacker.getStats().getValue(Stat.CRITICAL_DAMAGE, 1) * attacker.getStats().getPositionTypeValue(Stat.CRITICAL_DAMAGE, Position.getPosition(attacker, target));
            defenceCriticalDamage = target.getStats().getValue(Stat.DEFENCE_CRITICAL_DAMAGE, 1);
        }

        return Math.max(1, 2 + (criticalDamage - defenceCriticalDamage) / (75 + Math.min(defenceCriticalDamage, criticalDamage)));
    }

    /**
     * @param attacker
     * @param target
     * @param skill    {@code skill} to be used in the calculation, else calculation will result for autoattack.
     * @return critical damage additional bonus, not multiplier!
     */
    public static double calcCritDamageAdd(Creature attacker, Creature target, Skill skill) {
        final double criticalDamageAdd;
        final double defenceCriticalDamageAdd;

        if (skill != null) {
            if (skill.isMagic()) {
                // Magic critical damage.
                criticalDamageAdd = attacker.getStats().getValue(Stat.MAGIC_CRITICAL_DAMAGE_ADD, 0);
                defenceCriticalDamageAdd = target.getStats().getValue(Stat.DEFENCE_MAGIC_CRITICAL_DAMAGE_ADD, 0);
            } else {
                criticalDamageAdd = attacker.getStats().getValue(Stat.CRITICAL_DAMAGE_SKILL_ADD, 0);
                defenceCriticalDamageAdd = target.getStats().getValue(Stat.DEFENCE_CRITICAL_DAMAGE_SKILL_ADD, 0);
            }
        } else {
            // Autoattack critical damage.
            criticalDamageAdd = attacker.getStats().getValue(Stat.CRITICAL_DAMAGE_ADD, 0);
            defenceCriticalDamageAdd = target.getStats().getValue(Stat.DEFENCE_CRITICAL_DAMAGE_ADD, 0);
        }

        return criticalDamageAdd + defenceCriticalDamageAdd;
    }

    /**
     * @param target
     * @param dmg
     * @return true in case when ATTACK is canceled due to hit
     */
    public static boolean calcAtkBreak(Creature target, double dmg) {
        if (target.isChanneling()) {
            return false;
        }

        double init = 0;

        if (Config.ALT_GAME_CANCEL_CAST && target.isCastingNow(SkillCaster::canAbortCast)) {
            init = 15;
        }
        if (Config.ALT_GAME_CANCEL_BOW && target.isAttackingNow()) {
            final Weapon wpn = target.getActiveWeaponItem();
            if ((wpn != null) && (wpn.getItemType() == WeaponType.BOW)) {
                init = 15;
            }
        }

        if (target.isRaid() || target.isHpBlocked() || (init <= 0)) {
            return false; // No attack break
        }

        // Chance of break is higher with higher dmg
        init += Math.sqrt(13 * dmg);

        // Chance is affected by target MEN
        init -= ((BaseStats.MEN.calcBonus(target) * 100) - 100);

        // Calculate all modifiers for ATTACK_CANCEL
        double rate = target.getStats().getValue(Stat.ATTACK_CANCEL, init);

        // Adjust the rate to be between 1 and 99
        rate = max(Math.min(rate, 99), 1);

        return Rnd.get(100) < rate;
    }

    /**
     * Calculate delay (in milliseconds) for skills cast
     *
     * @param attacker
     * @param skill
     * @param skillTime
     * @return
     */
    public static int calcAtkSpd(Creature attacker, Skill skill, double skillTime) {
        if (skill.isMagic()) {
            return (int) ((skillTime / attacker.getMAtkSpd()) * 333);
        }
        return (int) ((skillTime / attacker.getPAtkSpd()) * 300);
    }

    public static double calcAtkSpdMultiplier(Creature creature) {
        double armorBonus = 1; // EquipedArmorSpeedByCrystal TODO: Implement me!
        double dexBonus = BaseStats.DEX.calcBonus(creature);
        double weaponAttackSpeed = Stat.weaponBaseValue(creature, Stat.PHYSICAL_ATTACK_SPEED) / armorBonus; // unk868
        double attackSpeedPerBonus = creature.getStats().getMul(Stat.PHYSICAL_ATTACK_SPEED);
        double attackSpeedDiffBonus = creature.getStats().getAdd(Stat.PHYSICAL_ATTACK_SPEED);
        return (dexBonus * (weaponAttackSpeed / 333) * attackSpeedPerBonus) + (attackSpeedDiffBonus / 333);
    }

    public static double calcMAtkSpdMultiplier(Creature creature) {
        final double armorBonus = 1; // TODO: Implement me!
        final double witBonus = BaseStats.WIT.calcBonus(creature);
        final double castingSpeedPerBonus = creature.getStats().getMul(Stat.MAGIC_ATTACK_SPEED);
        final double castingSpeedDiffBonus = creature.getStats().getAdd(Stat.MAGIC_ATTACK_SPEED);
        return ((1 / armorBonus) * witBonus * castingSpeedPerBonus) + (castingSpeedDiffBonus / 333);
    }

    /**
     * @param creature
     * @param skill
     * @return factor divisor for skill hit time and cancel time.
     */
    public static double calcSkillTimeFactor(Creature creature, Skill skill) {
        if (skill.isChanneling() || (skill.getSkillType() == SkillType.STATIC)) {
            return 1.0d;
        }

        double factor = 0.0;
        if (skill.getSkillType() == SkillType.MAGIC) {
            final double spiritshotHitTime = creature.isChargedShot(ShotType.SPIRITSHOTS) ? 0.4 : 0; // TODO: Implement propper values
            factor = creature.getStats().getMAttackSpeedMultiplier() + (creature.getStats().getMAttackSpeedMultiplier() * spiritshotHitTime); // matkspdmul + (matkspdmul * spiritshot_hit_time)
        } else {
            factor = creature.getAttackSpeedMultiplier();
        }

        if (isNpc(creature)) {
            double npcFactor = ((Npc) creature).getTemplate().getHitTimeFactorSkill();
            if (npcFactor > 0) {
                factor /= npcFactor;
            }
        }
        return max(0.01, factor);
    }

    public static double calcSkillCancelTime(Creature creature, Skill skill) {
        return max((skill.getHitCancelTime() * 1000) / calcSkillTimeFactor(creature, skill), SKILL_LAUNCH_TIME);
    }

    /**
     * Formula based on http://l2p.l2wh.com/nonskillattacks.html
     *
     * @param attacker
     * @param target
     * @return {@code true} if hit missed (target evaded), {@code false} otherwise.
     */
    public static boolean calcHitMiss(Creature attacker, Creature target) {
        int chance = (80 + (2 * (attacker.getAccuracy() - target.getEvasionRate()))) * 10;

        // Get additional bonus from the conditions when you are attacking
        chance *= HitConditionBonusData.getInstance().getConditionBonus(attacker, target);

        chance = max(chance, 200);
        chance = Math.min(chance, 980);

        return chance < Rnd.get(1000);
    }

    /**
     * Returns:<br>
     * 0 = shield defense doesn't succeed<br>
     * 1 = shield defense succeed<br>
     * 2 = perfect block<br>
     *
     * @param attacker
     * @param target
     * @param sendSysMsg
     * @return
     */
    public static byte calcShldUse(Creature attacker, Creature target, boolean sendSysMsg) {
        final ItemTemplate item = target.getSecondaryWeaponItem();
        if (!(item instanceof Armor) || (((Armor) item).getItemType() == ArmorType.SIGIL)) {
            return 0;
        }

        double shldRate = target.getStats().getValue(Stat.SHIELD_DEFENCE_RATE) * BaseStats.DEX.calcBonus(target);

        // if attacker use bow and target wear shield, shield block rate is multiplied by 1.3 (30%)
        if (attacker.getAttackType().isRanged()) {
            shldRate *= 1.3;
        }

        final int degreeside = target.isAffected(EffectFlag.PHYSICAL_SHIELD_ANGLE_ALL) ? 360 : 120;
        if ((degreeside < 360) && (abs(target.calculateDirectionTo(attacker) - convertHeadingToDegree(target.getHeading())) > (degreeside / 2.))) {
            return 0;
        }

        byte shldSuccess = SHIELD_DEFENSE_FAILED;

        // Check shield success
        if (shldRate > Rnd.get(100)) {
            // If shield succeed, check perfect block.
            if (((100 - (2 * BaseStats.DEX.calcBonus(target))) < Rnd.get(100))) {
                shldSuccess = SHIELD_DEFENSE_PERFECT_BLOCK;
            } else {
                shldSuccess = SHIELD_DEFENSE_SUCCEED;
            }
        }

        if (sendSysMsg && isPlayer(target)) {
            final Player enemy = target.getActingPlayer();

            switch (shldSuccess) {
                case SHIELD_DEFENSE_SUCCEED: {
                    enemy.sendPacket(SystemMessageId.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
                    break;
                }
                case SHIELD_DEFENSE_PERFECT_BLOCK: {
                    enemy.sendPacket(SystemMessageId.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
                    break;
                }
            }
        }

        return shldSuccess;
    }

    public static byte calcShldUse(Creature attacker, Creature target) {
        return calcShldUse(attacker, target, true);
    }

    public static boolean calcMagicAffected(Creature actor, Creature target, Skill skill) {
        // TODO: CHECK/FIX THIS FORMULA UP!!
        double defence = 0;
        if (skill.isActive() && skill.isBad()) {
            defence = target.getMDef();
        }

        final double attack = 2 * actor.getMAtk() * calcGeneralTraitBonus(actor, target, skill.getTrait(), false);
        double d = (attack - defence) / (attack + defence);

        if (skill.isDebuff()) {
            if (target.getAbnormalShieldBlocks() > 0) {
                target.decrementAbnormalShieldBlocks();
                return false;
            }
        }

        d += 0.5 * Rnd.nextGaussian();
        return d > 0;
    }

    public static double calcLvlBonusMod(Creature attacker, Creature target, Skill skill) {
        final int attackerLvl = skill.getMagicLevel() > 0 ? skill.getMagicLevel() : attacker.getLevel();
        final double skillLvlBonusRateMod = 1 + (skill.getLevelBonusRate() / 100.);
        final double lvlMod = 1 + ((attackerLvl - target.getLevel()) / 100.);
        return skillLvlBonusRateMod * lvlMod;
    }

    /**
     * Calculates the effect landing success.<br>
     *
     * @param attacker the attacker
     * @param target   the target
     * @param skill    the skill
     * @return {@code true} if the effect lands
     */
    public static boolean calcEffectSuccess(Creature attacker, Creature target, Skill skill) {
        // StaticObjects can not receive continuous effects.
        if (isDoor(target) || (target instanceof SiegeFlag) || (target instanceof StaticWorldObject)) {
            return false;
        }

        if (skill.isDebuff()) {
            boolean resisted = target.isCastingNow(s -> s.getSkill().getAbnormalResists().contains(skill.getAbnormalType()));
            if (!resisted) {
                if (target.getAbnormalShieldBlocks() > 0) {
                    target.decrementAbnormalShieldBlocks();
                    resisted = true;
                }
            }

            if (!resisted) {
                final double sphericBarrierRange = target.getStats().getValue(Stat.SPHERIC_BARRIER_RANGE, 0);
                if (sphericBarrierRange > 0) {
                    resisted = !MathUtil.isInsideRadius3D(attacker, target, (int) sphericBarrierRange);
                }
            }

            if (resisted) {
                attacker.sendPacket(getSystemMessage(SystemMessageId.C1_HAS_RESISTED_YOUR_S2).addString(target.getName()).addSkillName(skill));
                return false;
            }
        }

        final int activateRate = skill.getActivateRate();
        if (activateRate <= 0 || activateRate >= 100) {
            return true;
        }

        int magicLevel = skill.getMagicLevel();
        if (magicLevel <= -1) {
            magicLevel = target.getLevel() + 3;
        }

        final double targetBasicProperty = getAbnormalResist(skill.getBasicProperty(), target);
        final double baseMod = ((((((magicLevel - target.getLevel()) + 3) * skill.getLevelBonusRate()) + activateRate) + 30.0) - targetBasicProperty);
        final double elementMod = calcAttributeBonus(attacker, target, skill);
        final double traitMod = calcGeneralTraitBonus(attacker, target, skill.getTrait(), false);
        final double basicPropertyResist = getBasicPropertyResistBonus(skill.getBasicProperty(), target);
        final double buffDebuffMod = skill.isDebuff() ?  2d - target.getStats().getValue(Stat.RESIST_ABNORMAL_DEBUFF, 1) : 1;
        final double rate = baseMod * elementMod * traitMod * buffDebuffMod;
        final double finalRate = traitMod > 0 ? CommonUtil.constrain(rate, skill.getMinChance(), skill.getMaxChance()) * basicPropertyResist : 0;

        if (!Rnd.chance(finalRate)) {
            if(target != attacker) {
                attacker.sendPacket(getSystemMessage(SystemMessageId.C1_HAS_RESISTED_YOUR_S2).addString(target.getName()).addSkillName(skill));
            }
            return false;
        }
        return true;
    }

    public static boolean calcCubicSkillSuccess(CubicInstance attacker, Creature target, Skill skill, byte shld) {
        if (skill.isDebuff()) {
            if (skill.getActivateRate() == -1) {
                return true;
            }

            if (target.getAbnormalShieldBlocks() > 0) {
                target.decrementAbnormalShieldBlocks();
                return false;
            }
        }

        // Perfect Shield Block.
        if (shld == SHIELD_DEFENSE_PERFECT_BLOCK) {
            return false;
        }

        // if target reflect this skill then the effect will fail
        if (calcBuffDebuffReflection(target, skill)) {
            return false;
        }

        final double targetBasicProperty = getAbnormalResist(skill.getBasicProperty(), target);

        // Calculate BaseRate.
        final double baseRate = skill.getActivateRate();
        final double statMod = 1 + (targetBasicProperty / 100);
        double rate = (baseRate / statMod);

        // Resist Modifier.
        final double resMod = calcGeneralTraitBonus(attacker.getOwner(), target, skill.getTrait(), false);
        rate *= resMod;

        // Lvl Bonus Modifier.
        final double lvlBonusMod = calcLvlBonusMod(attacker.getOwner(), target, skill);
        rate *= lvlBonusMod;

        // Element Modifier.
        final double elementMod = calcAttributeBonus(attacker.getOwner(), target, skill);
        rate *= elementMod;

        final double basicPropertyResist = getBasicPropertyResistBonus(skill.getBasicProperty(), target);

        // Add Matk/Mdef Bonus (TODO: Pending)

        // Check the Rate Limits.
        final double finalRate = CommonUtil.constrain(rate, skill.getMinChance(), skill.getMaxChance()) * basicPropertyResist;

        return Rnd.get(100) < finalRate;
    }

    public static boolean calcMagicSuccess(Creature attacker, Creature target, Skill skill) {
        final int mAccDiff = attacker.getMagicAccuracy() - target.getMagicEvasionRate();
        int mAccModifier = 100;
        if (mAccDiff > -20) {
            mAccModifier = 2;
        } else if (mAccDiff > -25) {
            mAccModifier = 30;
        } else if (mAccDiff > -30) {
            mAccModifier = 60;
        } else if (mAccDiff > -35) {
            mAccModifier = 90;
        }

        float targetModifier = 1;
        if (isAttackable(target) && !target.isRaid() && !target.isRaidMinion() && (target.getLevel() >= Config.MIN_NPC_LVL_MAGIC_PENALTY) && (attacker.getActingPlayer() != null) && ((target.getLevel() - attacker.getActingPlayer().getLevel()) >= 3)) {
            final int lvlDiff = target.getLevel() - attacker.getActingPlayer().getLevel() - 2;
            if (lvlDiff >= Config.NPC_SKILL_CHANCE_PENALTY.size()) {
                targetModifier = Config.NPC_SKILL_CHANCE_PENALTY.get(Config.NPC_SKILL_CHANCE_PENALTY.size() - 1);
            } else {
                targetModifier = Config.NPC_SKILL_CHANCE_PENALTY.get(lvlDiff);
            }
        }
        // general magic resist
        final double resModifier = target.getStats().getValue(Stat.MAGIC_SUCCESS_RES, 1);
        final int rate = 100 - Math.round((float) (mAccModifier * targetModifier * resModifier));

        return (Rnd.get(100) < rate);
    }

    public static double calcManaDam(Creature attacker, Creature target, Skill skill, double power, boolean mcrit, double critLimit) {
        // Formula: (SQR(M.Atk)*Power*(Target Max MP/97))/M.Def
        double mAtk = attacker.getMAtk();
        double mDef = target.getMDef();
        final double mp = target.getMaxMp();

        switch (calcShldUse(attacker, target)) {
            case SHIELD_DEFENSE_SUCCEED -> mDef += target.getShldDef();
            case SHIELD_DEFENSE_PERFECT_BLOCK -> {
                return 1;
            }
        }

        mAtk *= skill.useSpiritShot() ? attacker.chargedShotBonus(ShotType.SPIRITSHOTS) : 1;

        double damage = (Math.sqrt(mAtk) * power * (mp / 97)) / mDef;
        damage *= calcGeneralTraitBonus(attacker, target, skill.getTrait(), false);
        damage *= calculatePvpPveBonus(attacker, target, skill, mcrit);

        // Failure calculation
        if (Config.ALT_GAME_MAGICFAILURES && !calcMagicSuccess(attacker, target, skill)) {
            if (isPlayer(attacker)) {
                final SystemMessage sm = getSystemMessage(SystemMessageId.DAMAGE_IS_DECREASED_BECAUSE_C1_RESISTED_C2_S_MAGIC);
                sm.addString(target.getName());
                sm.addString(attacker.getName());
                attacker.sendPacket(sm);
                damage /= 2;
            }

            if (isPlayer(target)) {
                final SystemMessage sm2 = getSystemMessage(SystemMessageId.C1_WEAKLY_RESISTED_C2_S_MAGIC);
                sm2.addString(target.getName());
                sm2.addString(attacker.getName());
                target.sendPacket(sm2);
            }
        }

        if (mcrit) {
            damage *= 3;
            damage = Math.min(damage, critLimit);
            attacker.sendPacket(SystemMessageId.M_CRITICAL);
        }
        return damage;
    }

    public static double calculateSkillResurrectRestorePercent(double baseRestorePercent, Creature caster) {
        if ((baseRestorePercent == 0) || (baseRestorePercent == 100)) {
            return baseRestorePercent;
        }

        double restorePercent = baseRestorePercent * BaseStats.WIT.calcBonus(caster);
        if ((restorePercent - baseRestorePercent) > 20.0) {
            restorePercent += 20.0;
        }

        restorePercent = max(restorePercent, baseRestorePercent);
        restorePercent = Math.min(restorePercent, 90.0);

        return restorePercent;
    }

    public static boolean calcPhysicalSkillEvasion(Creature activeChar, Creature target, Skill skill) {
        if (Rnd.get(100) < target.getStats().getSkillEvasionTypeValue(skill.getSkillType())) {
            if (isPlayer(activeChar)) {
                final SystemMessage sm = getSystemMessage(SystemMessageId.C1_DODGED_THE_ATTACK);
                sm.addString(target.getName());
                activeChar.getActingPlayer().sendPacket(sm);
            }
            if (isPlayer(target)) {
                final SystemMessage sm = getSystemMessage(SystemMessageId.YOU_HAVE_DODGED_C1_S_ATTACK);
                sm.addString(activeChar.getName());
                target.getActingPlayer().sendPacket(sm);
            }
            return true;
        }
        return false;
    }

    public static boolean calcSkillMastery(Creature actor, Skill skill) {
        // Static Skills and Item skills  are not affected by Skill Mastery.
        if (!isPlayer(actor)) {
            return false;
        }

        final int val = (int) actor.getStats().getAdd(Stat.SKILL_CRITICAL, -1);

        if (val == -1) {
            return false;
        }

        final double chance = BaseStats.values()[val].calcBonus(actor) * actor.getStats().getValue(Stat.SKILL_CRITICAL_PROBABILITY, 1);

        return ((Rnd.nextDouble() * 100.) < chance);
    }

    /**
     * Calculates the attribute bonus with the following formula: <BR>
     * diff > 0, so AttBonus = 1,025 + sqrt[(diff^3) / 2] * 0,0001, cannot be above 1,25! <BR>
     * diff < 0, so AttBonus = 0,975 - sqrt[(diff^3) / 2] * 0,0001, cannot be below 0,75! <BR>
     * diff == 0, so AttBonus = 1<br>
     * It has been tested that physical skills do get affected by attack attribute even<br>
     * if they don't have any attribute. In that case only the biggest attack attribute is taken.
     *
     * @param attacker
     * @param target
     * @param skill    Can be {@code null} if there is no skill used for the attack.
     * @return The attribute bonus
     */
    public static double calcAttributeBonus(Creature attacker, Creature target, Skill skill) {
        int attack_attribute;
        int defence_attribute;

        if ((skill != null) && (skill.getAttributeType() != AttributeType.NONE)) {
            attack_attribute = attacker.getAttackElementValue(skill.getAttributeType()) + skill.getAttributeValue();
            defence_attribute = target.getDefenseElementValue(skill.getAttributeType());
        } else {
            attack_attribute = attacker.getAttackElementValue(attacker.getAttackElement());
            defence_attribute = target.getDefenseElementValue(attacker.getAttackElement());
        }

        final int diff = attack_attribute - defence_attribute;
        if (diff > 0) {
            return Math.min(1.025 + (Math.sqrt(Math.pow(diff, 3) / 2) * 0.0001), 1.25);
        } else if (diff < 0) {
            return max(0.975 - (Math.sqrt(Math.pow(-diff, 3) / 2) * 0.0001), 0.75);
        }

        return 1;
    }

    public static void calcCounterAttack(Creature attacker, Creature target, Skill skill, boolean crit) {
        // Only melee skills can be reflected
        if (skill.isMagic() || (skill.getCastRange() > MELEE_ATTACK_RANGE)) {
            return;
        }

        final double chance = target.getStats().getValue(Stat.VENGEANCE_SKILL_PHYSICAL_DAMAGE, 0);
        if (Rnd.get(100) < chance) {
            if (isPlayer(target)) {
                final SystemMessage sm = getSystemMessage(SystemMessageId.YOU_COUNTERED_C1_S_ATTACK);
                sm.addString(attacker.getName());
                target.sendPacket(sm);
            }
            if (isPlayer(attacker)) {
                final SystemMessage sm = getSystemMessage(SystemMessageId.C1_IS_PERFORMING_A_COUNTERATTACK);
                sm.addString(target.getName());
                attacker.sendPacket(sm);
            }

            double counterdmg = ((target.getPAtk() * 873.) / attacker.getPDef()); // Old: (((target.getPAtk(attacker) * 10.0) * 70.0) / attacker.getPDef(target));
            counterdmg *= calcWeaponTraitBonus(attacker, target);
            counterdmg *= calcGeneralTraitBonus(attacker, target, skill.getTrait(), true);
            counterdmg *= calcAttributeBonus(attacker, target, skill);

            attacker.reduceCurrentHp(counterdmg, target, skill, DamageType.ATTACK);
        }
    }

    /**
     * Calculate buff/debuff reflection.
     *
     * @param target
     * @param skill
     * @return {@code true} if reflect, {@code false} otherwise.
     */
    public static boolean calcBuffDebuffReflection(Creature target, Skill skill) {
        if (!skill.isDebuff() || (skill.getActivateRate() == -1)) {
            return false;
        }
        return target.getStats().getValue(skill.isMagic() ? Stat.REFLECT_SKILL_MAGIC : Stat.REFLECT_SKILL_PHYSIC, 0) > Rnd.get(100);
    }

    /**
     * Calculate damage caused by falling
     *
     * @param cha
     * @param fallHeight
     * @return damage
     */
    public static double calcFallDam(Creature cha, int fallHeight) {
        if (!Config.ENABLE_FALLING_DAMAGE || (fallHeight < 0)) {
            return 0;
        }
        return cha.getStats().getValue(Stat.FALL, (fallHeight * cha.getMaxHp()) / 1000.0);
    }

    /**
     * Basic chance formula:<br>
     * <ul>
     * <li>chance = weapon_critical * dex_bonus * crit_height_bonus * crit_pos_bonus * effect_bonus * fatal_blow_rate</li>
     * <li>weapon_critical = (12 for daggers)</li>
     * <li>dex_bonus = dex modifier bonus for current dex (Seems unused in GOD, so its not used in formula).</li>
     * <li>crit_height_bonus = (z_diff * 4 / 5 + 10) / 100 + 1 or alternatively (z_diff * 0.008) + 1.1. Be aware of z_diff constraint of -25 to 25.</li>
     * <li>crit_pos_bonus = crit_pos(front = 1, side = 1.1, back = 1.3) * p_critical_rate_position_bonus</li>
     * <li>effect_bonus = (p2 + 100) / 100, p2 - 2nd param of effect. Blow chance of effect.</li>
     * </ul>
     * Chance cannot be higher than 80%.
     *
     * @param activeChar
     * @param target
     * @param skill
     * @param chanceBoost
     * @return
     */
    public static boolean calcBlowSuccess(Creature activeChar, Creature target, Skill skill, double chanceBoost) {
        if(skill.getActivateRate() == -1) {
            return true;
        }
        final Weapon weapon = activeChar.getActiveWeaponItem();
        final double weaponCritical = weapon != null ? weapon.getStats(Stat.CRITICAL_RATE, activeChar.getTemplate().getBaseCritRate()) : activeChar.getTemplate().getBaseCritRate();
        double dexBonus = BaseStats.DEX.calcBonus(activeChar);
        final double critHeightBonus = calcCriticalHeightBonus(activeChar, target);
        final double criticalPosition = calcCriticalPositionBonus(activeChar, target); // 30% chance from back, 10% chance from side. Include buffs that give positional crit rate.
        final double chanceBoostMod = (100 + chanceBoost) / 100;
        final double blowRateMod = activeChar.getStats().getValue(Stat.BLOW_RATE, 1);

        final double rate = criticalPosition * critHeightBonus * weaponCritical * chanceBoostMod * blowRateMod * dexBonus;

        // Blow rate is capped at 80%
        return Rnd.get(100) < Math.min(rate, 80);
    }

    public static List<BuffInfo> calcCancelStealEffects(Creature activeChar, Creature target, Skill skill, DispelSlotType slot, int rate, int max) {
        final List<BuffInfo> canceled = new ArrayList<>(max);
        switch (slot) {
            case BUFF -> {
                // Resist Modifier.
                final int cancelMagicLvl = skill.getMagicLevel();

                // Prevent initialization.
                final List<BuffInfo> dances = target.getEffectList().getDances();
                for (int i = dances.size() - 1; i >= 0; i--) // reverse order
                {
                    final BuffInfo info = dances.get(i);
                    if (!info.getSkill().canBeStolen() || ((rate < 100) && !calcCancelSuccess(info, cancelMagicLvl, rate, skill, target))) {
                        continue;
                    }
                    canceled.add(info);
                    if (canceled.size() >= max) {
                        break;
                    }
                }

                if (canceled.size() < max) {
                    // Prevent initialization.
                    final List<BuffInfo> buffs = target.getEffectList().getBuffs();
                    for (int i = buffs.size() - 1; i >= 0; i--) // reverse order
                    {
                        final BuffInfo info = buffs.get(i);
                        if (!info.getSkill().canBeStolen() || ((rate < 100) && !calcCancelSuccess(info, cancelMagicLvl, rate, skill, target))) {
                            continue;
                        }
                        canceled.add(info);
                        if (canceled.size() >= max) {
                            break;
                        }
                    }
                }
            }
            case DEBUFF -> {
                final List<BuffInfo> debuffs = target.getEffectList().getDebuffs();
                for (int i = debuffs.size() - 1; i >= 0; i--) {
                    final BuffInfo info = debuffs.get(i);
                    if (info.getSkill().canBeDispelled() && (Rnd.get(100) <= rate)) {
                        canceled.add(info);
                        if (canceled.size() >= max) {
                            break;
                        }
                    }
                }
            }
        }
        return canceled;
    }

    public static boolean calcCancelSuccess(BuffInfo info, int cancelMagicLvl, int rate, Skill skill, Creature target) {
        final var resist =  2d - target.getStats().getValue(Stat.RESIST_DISPEL_BUFF, 1);
        final int chance = (int) (rate + ((cancelMagicLvl - info.getSkill().getMagicLevel()) * 2) + ((info.getAbnormalTime() / 120) * resist));
        return Rnd.get(100) < CommonUtil.constrain(chance, 25, 75); // TODO: i_dispel_by_slot_probability min = 40, max = 95.
    }

    /**
     * Calculates the abnormal time for an effect.<br>
     * The abnormal time is taken from the skill definition, and it's global for all effects present in the skills.
     *
     * @param caster the caster
     * @param target the target
     * @param skill  the skill
     * @return the time that the effect will last
     */
    public static int calcEffectAbnormalTime(Creature caster, Creature target, Skill skill) {
        int time = (skill == null) || skill.isPassive() || skill.isToggle() ? -1 : skill.getAbnormalTime();

        // If the skill is a mastery skill, the effect will last twice the default time.
        if ((skill != null) && calcSkillMastery(caster, skill)) {
            time *= 2;
        }

        return time;
    }

    /**
     * Calculate Probability in following effects:<br>
     * TargetCancel,<br>
     * TargetMeProbability,<br>
     * SkillTurning,<br>
     * Betray,<br>
     * Bluff,<br>
     * DeleteHate,<br>
     * RandomizeHate,<br>
     * DeleteHateOfMe,<br>
     * TransferHate,<br>
     * Confuse<br>
     * Compelling,<br>
     * Knockback<br>
     * Pull<br>
     *
     * @param baseChance chance from effect parameter
     * @param attacker
     * @param target
     * @param skill
     * @return chance for effect to succeed
     */
    public static boolean calcProbability(double baseChance, Creature attacker, Creature target, Skill skill) {
        // Skills without set probability should only test against trait invulnerability.
        if (Double.isNaN(baseChance)) {
            return calcGeneralTraitBonus(attacker, target, skill.getTrait(), true) > 0;
        }

        // Outdated formula: return Rnd.get(100) < ((((((skill.getMagicLevel() + baseChance) - target.getLevel()) + 30) - target.getINT()) * calcAttributeBonus(attacker, target, skill)) * calcGeneralTraitBonus(attacker, target, skill.getTraitType(), false));
        // TODO: Find more retail-like formula
        return Rnd.get(100) < (((((skill.getMagicLevel() + baseChance) - target.getLevel()) - getAbnormalResist(skill.getBasicProperty(), target)) * calcAttributeBonus(attacker, target, skill)) * calcGeneralTraitBonus(attacker, target, skill.getTrait(), false));
    }

    /**
     * Calculates karma lost upon death.
     *
     * @param player
     * @param finalExp
     * @return the amount of karma player has loosed.
     */
    public static int calculateKarmaLost(Player player, double finalExp) {
        final double karmaLooseMul = KarmaData.getInstance().getMultiplier(player.getLevel());
        if (finalExp > 0) // Received exp
        {
            finalExp /= Config.RATE_KARMA_LOST;
        }
        return (int) ((abs(finalExp) / karmaLooseMul) / 30);
    }

    /**
     * Calculates karma gain upon playable kill.</br>
     * Updated to High Five on 10.09.2014 by Zealar tested in retail.
     *
     * @param pkCount
     * @param isSummon
     * @return karma points that will be added to the player.
     */
    public static int calculateKarmaGain(int pkCount, boolean isSummon) {
        int result = 43200;

        if (isSummon) {
            result = (int) ((((pkCount * 0.375) + 1) * 60) * 4) - 150;

            if (result > 10800) {
                return 10800;
            }
        }

        if (pkCount < 99) {
            result = (int) ((((pkCount * 0.5) + 1) * 60) * 12);
        } else if (pkCount < 180) {
            result = (int) ((((pkCount * 0.125) + 37.75) * 60) * 12);
        }

        return result;
    }

    public static double calcGeneralTraitBonus(Creature attacker, Creature target, TraitType traitType, boolean ignoreResistance) {
        if (traitType == TraitType.NONE) {
            return 1.0;
        }

        if (target.getStats().isInvulnerableTrait(traitType)) {
            return 0;
        }

        switch (traitType.getType()) {
            case 2: {
                if (!attacker.getStats().hasAttackTrait(traitType) || !target.getStats().hasDefenceTrait(traitType)) {
                    return 1.0;
                }
                break;
            }
            case 3: {
                if (ignoreResistance) {
                    return 1.0;
                }
                break;
            }
            default: {
                return 1.0;
            }
        }

        return max(attacker.getStats().getAttackTrait(traitType) - target.getStats().getDefenceTrait(traitType), 0.05);
    }

    public static double calcWeaknessBonus(Creature attacker, Creature target, TraitType traitType)
    {
        double result = 1;
        for (TraitType trait : TraitType.getAllWeakness())
        {
            if ((traitType != trait) && target.getStats().hasDefenceTrait(trait) && attacker.getStats().hasAttackTrait(trait) && !target.getStats().isInvulnerableTrait(traitType))
            {
                result *= max(attacker.getStats().getAttackTrait(trait) - target.getStats().getDefenceTrait(trait), 0.05);
            }
        }
        return result;
    }

    public static double calcWeaponTraitBonus(Creature attacker, Creature target) {
        return max(0, 1.0 - target.getStats().getDefenceTrait(attacker.getAttackType().getTraitType()));
    }

    public static double calcAttackTraitBonus(Creature attacker, Creature target) {
        final double weaponTraitBonus = calcWeaponTraitBonus(attacker, target);
        if (weaponTraitBonus == 0) {
            return 0;
        }

        double weaknessBonus = 1.0;
        for (TraitType traitType : TraitType.values()) {
            if (traitType.getType() == 2) {
                weaknessBonus *= calcGeneralTraitBonus(attacker, target, traitType, true);
                if (weaknessBonus == 0) {
                    return 0;
                }
            }
        }

        return max(weaponTraitBonus * weaknessBonus, 0.05);
    }

    public static double getBasicPropertyResistBonus(BasicProperty basicProperty, Creature target) {
        if ((basicProperty == BasicProperty.NONE) || !target.hasBasicPropertyResist()) {
            return 1.0;
        }

        final BasicPropertyResist resist = target.getBasicPropertyResist(basicProperty);
        switch (resist.getResistLevel()) {
            case 0: {
                return 1.0;
            }
            case 1: {
                return 0.6;
            }
            case 2: {
                return 0.3;
            }
            default: {
                return 0;
            }
        }
    }

    /**
     * Calculated damage caused by ATTACK of attacker on target.
     *
     * @param attacker player or NPC that makes ATTACK
     * @param target   player or NPC, target of ATTACK
     * @param shld
     * @param crit     if the ATTACK have critical success
     * @param ss       if weapon item was charged by soulshot
     * @return
     */
    public static double calcAutoAttackDamage(Creature attacker, Creature target, byte shld, boolean crit, boolean ss) {
        // DEFENCE CALCULATION (pDef + sDef)
        double defence = target.getPDef();

        switch (shld) {
            case SHIELD_DEFENSE_SUCCEED: {
                defence += target.getShldDef();
                break;
            }
            case SHIELD_DEFENSE_PERFECT_BLOCK: {
                return 1.;
            }
        }

        final Weapon weapon = attacker.getActiveWeaponItem();
        final boolean isRanged = (weapon != null) && weapon.getItemType().isRanged();

        final double cAtk = crit ? calcCritDamage(attacker, target, null) : 1;
        final double cAtkAdd = crit ? calcCritDamageAdd(attacker, target, null) : 0;
        final double critMod = crit ? (isRanged ? 0.5 : 1) : 0;
        final double ssBonus = ss ? attacker.chargedShotBonus(ShotType.SOULSHOTS) : 1;
        final double random_damage = attacker.getRandomDamageMultiplier();
        final double proxBonus = (attacker.isInFrontOf(target) ? 0 : (attacker.isBehind(target) ? 0.2 : 0.05)) * attacker.getPAtk();
        double attack = (attacker.getPAtk() * random_damage) + proxBonus;

        // ....................______________Critical Section___________________...._______Non-Critical Section______
        // ATTACK CALCULATION (((pAtk * cAtk * ss + cAtkAdd) * crit) * weaponMod) + (pAtk (1 - crit) * ss * weaponMod)
        // ````````````````````^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^````^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        attack = ((((attack * cAtk * ssBonus) + cAtkAdd) * critMod) * (isRanged ? 154 : 77)) + (attack * (1 - critMod) * ssBonus * (isRanged ? 154 : 77));

        // DAMAGE CALCULATION (ATTACK / DEFENCE) * trait bonus * attr bonus * pvp bonus * pve bonus
        double damage = attack / defence;
        damage *= calcAttackTraitBonus(attacker, target);
        damage *= calcAttributeBonus(attacker, target, null);
        damage *= calculatePvpPveBonus(attacker, target, null, crit);

        damage = max(0, damage);

        return damage;
    }

    public static double getAbnormalResist(BasicProperty basicProperty, Creature target) {
        switch (basicProperty) {
            case PHYSIC: {
                return target.getStats().getValue(Stat.ABNORMAL_RESIST_PHYSICAL);
            }
            case MAGIC: {
                return target.getStats().getValue(Stat.ABNORMAL_RESIST_MAGICAL);
            }
            default: {
                return 0;
            }
        }
    }

    public static double calcPveDamagePenalty(Creature attacker, Creature target, Skill skill, boolean crit) {
        if (isAttackable(target) && (target.getLevel() >= Config.MIN_NPC_LVL_DMG_PENALTY) && (attacker.getActingPlayer() != null) && ((target.getLevel() - attacker.getActingPlayer().getLevel()) > 1)) {
            final int lvlDiff = target.getLevel() - attacker.getActingPlayer().getLevel() - 1;
            if (skill != null) {
                return Config.NPC_SKILL_DMG_PENALTY.get(Math.min(lvlDiff, Config.NPC_SKILL_DMG_PENALTY.size() - 1));
            } else if (crit) {
                return Config.NPC_CRIT_DMG_PENALTY.get(Math.min(lvlDiff, Config.NPC_CRIT_DMG_PENALTY.size() - 1));
            }

            return Config.NPC_DMG_PENALTY.get(Math.min(lvlDiff, Config.NPC_DMG_PENALTY.size() - 1));
        }

        return 1.0;
    }

    /**
     * Calculates if the specified creature can get its stun effect removed due to damage taken.
     *
     * @param activeChar the character to be checked
     * @return {@code true} if character should get its stun effects removed, {@code false} otherwise.
     */
    public static boolean calcStunBreak(Creature activeChar) {
        // Check if target is stunned and break it with 14% chance. (retail is 14% and 35% on crit?)
        if (Config.ALT_GAME_STUN_BREAK && activeChar.hasBlockActions() && (Rnd.get(14) == 0)) {
            // Any stun that has double duration due to skill mastery, doesn't get removed until its time reaches the usual abnormal time.
            return activeChar.getEffectList().hasAbnormalType(AbnormalType.STUN, info -> info.getTime() <= info.getSkill().getAbnormalTime());
        }
        return false;
    }

    public static boolean calcRealTargetBreak() {
        return Rnd.chance(3);
    }

    /**
     * @param attackSpeed the attack speed of the Creature.
     * @return {@code 500000 / attackSpeed}.
     */
    public static int calculateTimeBetweenAttacks(int attackSpeed) {
        // Measured Nov 2015 by Nik. Formula: atk.spd/500 = hits per second.
        return max(50, (500000 / attackSpeed));
    }

    /**
     * @param totalAttackTime the time needed to make a full attack.
     * @param attackType      the weapon type used for attack.
     * @param twoHanded       if the weapon is two handed.
     * @param secondHit       calculates the second hit for dual attacks.
     * @return the time required from the start of the attack until you hit the target.
     */
    public static int calculateTimeToHit(int totalAttackTime, WeaponType attackType, boolean twoHanded, boolean secondHit) {
        // Gracia Final Retail confirmed:
        // Time to damage (1 hand, 1 hit): TotalBasicAttackTime * 0.644
        // Time to damage (2 hand, 1 hit): TotalBasicAttackTime * 0.735
        // Time to damage (2 hand, 2 hit): TotalBasicAttackTime * 0.2726 and TotalBasicAttackTime * 0.6
        // Time to damage (bow/xbow): TotalBasicAttackTime * 0.978

        // Measured July 2016 by Nik.
        // Due to retail packet delay, we are unable to gather too accurate results. Therefore the below formulas are based on original Gracia Final values.
        // Any original values that appear higher than tested have been replaced with the tested values, because even with packet delay its obvious they are wrong.
        // All other original values are compared with the test results and differences are considered to be too insignificant and mostly caused due to packet delay.
        switch (attackType) {
            case BOW:
            case CROSSBOW:
            case TWO_HAND_CROSSBOW: {
                return (int) (totalAttackTime * 0.95);
            }
            case DUAL_BLUNT:
            case DUAL_DAGGER:
            case DUAL:
            case FIST: {
                if (secondHit) {
                    return (int) (totalAttackTime * 0.6);
                }

                return (int) (totalAttackTime * 0.2726);
            }
            default: {
                if (twoHanded) {
                    return (int) (totalAttackTime * 0.735);
                }

                return (int) (totalAttackTime * 0.644);
            }
        }
    }

    public static int calculateReuseTime(Creature activeChar, Weapon weapon) {
        if (weapon == null) {
            return 0;
        }

        final WeaponType defaultAttackType = weapon.getItemType();
        final WeaponType weaponType = activeChar.getTransformation().map(transform -> transform.getBaseAttackType(activeChar, defaultAttackType)).orElse(defaultAttackType);
        int reuse = weapon.getReuseDelay();

        // only bows should continue for now
        if ((reuse == 0) || !weaponType.isRanged()) {
            return 0;
        }

        reuse *= activeChar.getStats().getWeaponReuseModifier();
        double atkSpd = activeChar.getStats().getPAtkSpd();

        return (int) ((500000 + (333 * reuse)) / atkSpd);
    }

    public static double calculatePvpPveBonus(Creature attacker, Creature target, Skill skill, boolean crit) {
        // PvP bonus
        if (isPlayable(attacker) && isPlayable(target)) {
            double pvpAttack;
            final double pvpDefense;
            if (skill != null) {
                if (skill.isMagic()) {
                    // Magical Skill PvP
                    pvpAttack = attacker.getStats().getValue(Stat.PVP_MAGICAL_SKILL_DAMAGE, 1);
                    pvpDefense = target.getStats().getValue(Stat.PVP_MAGICAL_SKILL_DEFENCE, 1);
                } else {
                    // Physical Skill PvP
                    pvpAttack = attacker.getStats().getValue(Stat.PVP_PHYSICAL_SKILL_DAMAGE, 1);
                    pvpDefense = target.getStats().getValue(Stat.PVP_PHYSICAL_SKILL_DEFENCE, 1);
                }
            } else {
                // Autoattack PvP
                pvpAttack = attacker.getStats().getValue(Stat.PVP_PHYSICAL_ATTACK_DAMAGE, 1);
                pvpDefense = target.getStats().getValue(Stat.PVP_PHYSICAL_ATTACK_DEFENCE, 1);
            }
            return max(0.05, 1 + (pvpAttack - pvpDefense));
        }

        // PvE Bonus
        if (isAttackable(target) || isAttackable(attacker)) {
            double pveAttack;
            final double pveDefense;
            final double pveRaidDefense;
            final double pveRaidAttack;
            final double pvePenalty = calcPveDamagePenalty(attacker, target, skill, crit);

            if (skill != null) {
                if (skill.isMagic()) {
                    // Magical Skill PvE
                    pveAttack = attacker.getStats().getValue(Stat.PVE_MAGICAL_SKILL_DAMAGE, 1);
                    pveDefense = target.getStats().getValue(Stat.PVE_MAGICAL_SKILL_DEFENCE, 1);
                    pveRaidDefense = attacker.isRaid() ? target.getStats().getValue(Stat.PVE_RAID_MAGICAL_SKILL_DEFENCE, 1) : 1;
                    pveRaidAttack = attacker.isRaid() ? attacker.getStats().getValue(Stat.PVE_RAID_MAGICAL_SKILL_DAMAGE, 1) : 1;
                } else {
                    // Physical Skill PvE
                    pveAttack = attacker.getStats().getValue(Stat.PVE_PHYSICAL_SKILL_DAMAGE, 1);
                    pveDefense = target.getStats().getValue(Stat.PVE_PHYSICAL_SKILL_DEFENCE, 1);
                    pveRaidAttack = attacker.isRaid() ? attacker.getStats().getValue(Stat.PVE_RAID_PHYSICAL_SKILL_DAMAGE, 1) : 1;
                    pveRaidDefense = attacker.isRaid() ? target.getStats().getValue(Stat.PVE_RAID_PHYSICAL_SKILL_DEFENCE, 1) : 1;
                }
            } else {
                // Autoattack PvE
                pveAttack = attacker.getStats().getValue(Stat.PVE_PHYSICAL_ATTACK_DAMAGE, 1);
                pveDefense = target.getStats().getValue(Stat.PVE_PHYSICAL_ATTACK_DEFENCE, 1);
                pveRaidAttack = attacker.isRaid() ? attacker.getStats().getValue(Stat.PVE_RAID_PHYSICAL_ATTACK_DAMAGE, 1) : 1;
                pveRaidDefense = attacker.isRaid() ? target.getStats().getValue(Stat.PVE_RAID_PHYSICAL_ATTACK_DEFENCE, 1) : 1;
            }
            //return max(0.05, (1 + (pveAttack - (pveDefense * pveRaidDefense))) * pvePenalty);
            return Math.max(0.05, (1 + ((pveAttack * pveRaidAttack) - (pveDefense * pveRaidDefense))) * pvePenalty); // Bonus should not be negative.
        }

        return 1;
    }

    public static double calcSpiritElementalDamage(Creature attacker, Creature target, double baseDamage) {
        // TODO find retail calc
        if(isPlayer(attacker)) {
            var attackerPlayer = attacker.getActingPlayer();
            ElementalType type = ElementalType.of(attackerPlayer.getActiveElementalSpiritType());

            var elementalDamage = 0;

            if(ElementalType.NONE == type) {
                return elementalDamage;
            }

            var critRate = attackerPlayer.getElementalSpiritCritRate();
            var isCrit = Math.min(critRate * 10, 380) > Rnd.get(1000);
            var critDamage = attackerPlayer.getElementalSpiritCritDamage();
            var attack = attackerPlayer.getActiveElementalSpiritAttack() - target.getElementalSpiritDefenseOf(type) + Rnd.get(-2, 6);
            if(isPlayer(target)) {
                return calcSpiritElementalPvPDamage(attack, critDamage, isCrit, baseDamage);
            }
            return calcSpiritElementalPvEDamage(type, target.getElementalSpiritType(), attack, critDamage, isCrit, baseDamage);
        }

        return 0;
    }

    private static double calcSpiritElementalPvPDamage(double attack, double critDamage, boolean isCrit, double baseDamage) {
        var damage = min(max(0, (attack * 1.3 + baseDamage * 0.03 * attack ) / log(max(attack, 5))), 2295);
        if(isCrit) {
            damage *= 1 + (Rnd.get(13, 20) + critDamage) / 100;
        }
        return damage;
    }

    private static double calcSpiritElementalPvEDamage(ElementalType attackerType, ElementalType targetType, double attack, double critDamage, boolean isCrit, double baseDamage) {
        double damage = abs(attack * 0.8);
        double bonus;

       if(attackerType.isSuperior(targetType)) {
            damage *= 1.3;
            bonus = 1.3;
        } else if(targetType == attackerType) {
            bonus = 1.1;
        } else {
            damage *= 1.1;
            bonus = 1.1;
        }

        if(isCrit) {
           damage += abs((40 + (9.2 + attack * 0.048 ) * critDamage) * bonus + Rnd.get(-10, 30)) ;
        }

        return ((damage + baseDamage) * bonus) / log(20 + baseDamage + damage);
    }

}
