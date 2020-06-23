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

import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.finalizers.*;
import org.l2j.gameserver.util.MathUtil;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * Enum of basic stats.
 *
 * @author mkizub, UnAfraid, NosBit, Sdw
 * @author JoeAlisson
 */
public enum Stat {
    // HP, MP & CP
    MAX_HP(new MaxHpFinalizer()),
    MAX_MP(new MaxMpFinalizer()),
    MAX_CP(new MaxCpFinalizer()),
    MAX_RECOVERABLE_HP, // The maximum HP that is able to be recovered trough heals
    MAX_RECOVERABLE_MP,
    MAX_RECOVERABLE_CP,
    REGENERATE_HP_RATE(new RegenHPFinalizer()),
    REGENERATE_CP_RATE(new RegenCPFinalizer()),
    REGENERATE_MP_RATE(new RegenMPFinalizer()),
    ADDITIONAL_POTION_HP,
    ADDITIONAL_POTION_MP,
    ADDITIONAL_POTION_CP,
    MANA_CHARGE,
    HEAL_RECEIVE,
    HEAL_POWER,

    // ATTACK & DEFENCE
    PHYSICAL_DEFENCE(new PDefenseFinalizer()),
    MAGICAL_DEFENCE(new MDefenseFinalizer()),
    PHYSICAL_ATTACK(new PAttackFinalizer()),
    MAGIC_ATTACK(new MAttackFinalizer()),
    PHYSICAL_ATTACK_SPEED(new PAttackSpeedFinalizer()),
    MAGIC_ATTACK_SPEED(new MAttackSpeedFinalizer()), // Magic Skill Casting Time Rate
    ATK_REUSE, // Bows Hits Reuse Rate
    SHIELD_DEFENCE(new ShieldDefenceFinalizer()),
    CRITICAL_DAMAGE,
    CRITICAL_DAMAGE_ADD, // this is another type for special critical damage mods - vicious stance, critical power and critical damage SA
    HATE_ATTACK,
    REAR_DAMAGE_RATE,

    DAMAGE_IMMOBILIZED,
    DAMAGE_TAKEN_IMMOBILIZED,

    // PVP BONUS,
    PVP_PHYSICAL_ATTACK_DAMAGE,
    PVP_MAGICAL_SKILL_DAMAGE,
    PVP_PHYSICAL_SKILL_DAMAGE,
    PVP_PHYSICAL_ATTACK_DEFENCE,
    PVP_MAGICAL_SKILL_DEFENCE,
    PVP_PHYSICAL_SKILL_DEFENCE,

    // PVE BONUS,
    PVE_PHYSICAL_ATTACK_DAMAGE,
    PVE_PHYSICAL_SKILL_DAMAGE,
    PVE_MAGICAL_SKILL_DAMAGE,
    PVE_PHYSICAL_ATTACK_DEFENCE,
    PVE_PHYSICAL_SKILL_DEFENCE,
    PVE_MAGICAL_SKILL_DEFENCE,
    PVE_RAID_PHYSICAL_ATTACK_DEFENCE,
    PVE_RAID_PHYSICAL_SKILL_DEFENCE,
    PVE_RAID_MAGICAL_SKILL_DEFENCE,
    PVE_RAID_PHYSICAL_ATTACK_DAMAGE,
    PVE_RAID_PHYSICAL_SKILL_DAMAGE,
    PVE_RAID_MAGICAL_SKILL_DAMAGE,

    // FIXED BONUS,
    PVP_DAMAGE_TAKEN,
    PVE_DAMAGE_TAKEN,
    PVE_DAMAGE_TAKEN_MONSTER,
    PVE_DAMAGE_TAKEN_RAID,
    DAMAGE_TAKEN,

    // ATTACK & DEFENCE RATES
    MAGIC_CRITICAL_DAMAGE,
    PHYSICAL_SKILL_POWER, // Adding skill power (not multipliers) results in points added directly to final value unmodified by defence, traits, elements, criticals etc.
    // Even when damage is 0 due to general trait immune multiplier, added skill power is active and clearly visible (damage not being 0 but at the value of added skill power).
    MAGICAL_SKILL_POWER,
    CRITICAL_DAMAGE_SKILL,
    CRITICAL_DAMAGE_SKILL_ADD,
    MAGIC_CRITICAL_DAMAGE_ADD,
    SHIELD_DEFENCE_RATE(new ShieldDefenceRateFinalizer()),
    CRITICAL_RATE(new PCriticalRateFinalizer()),
    CRITICAL_RATE_SKILL,
    MAGIC_CRITICAL_RATE(new MCritRateFinalizer()),
    BLOW_RATE,
    DEFENCE_CRITICAL_RATE,
    DEFENCE_CRITICAL_RATE_ADD,
    DEFENCE_MAGIC_CRITICAL_RATE,
    DEFENCE_MAGIC_CRITICAL_RATE_ADD,
    DEFENCE_CRITICAL_DAMAGE,
    DEFENCE_MAGIC_CRITICAL_DAMAGE,
    DEFENCE_MAGIC_CRITICAL_DAMAGE_ADD,
    DEFENCE_CRITICAL_DAMAGE_ADD, // Resistance to critical damage in value (Example: +100 will be 100 more critical damage, NOT 100% more).
    DEFENCE_CRITICAL_DAMAGE_SKILL,
    DEFENCE_CRITICAL_DAMAGE_SKILL_ADD,
    INSTANT_KILL_RESIST,
    EXPSP_RATE,
    BONUS_EXP,
    BONUS_SP,
    BONUS_DROP_AMOUNT,
    BONUS_DROP_RATE,
    BONUS_SPOIL_RATE,
    ATTACK_CANCEL,
    BONUS_L2COIN_DROP_RATE,

    // ACCURACY & RANGE
    ACCURACY(new PAccuracyFinalizer()),
    ACCURACY_MAGIC(new MAccuracyFinalizer()),
    EVASION_RATE(new PEvasionRateFinalizer()),
    MAGIC_EVASION_RATE(new MEvasionRateFinalizer()),
    PHYSICAL_ATTACK_RANGE(new PRangeFinalizer()),
    MAGIC_ATTACK_RANGE,
    ATTACK_COUNT_MAX,
    PHYSICAL_POLEARM_TARGET_SINGLE,
    HIT_AT_NIGHT,

    // Run speed, walk & escape speed are calculated proportionally, magic speed is a buff
    SPEED,
    RUN_SPEED(new SpeedFinalizer()),
    WALK_SPEED(new SpeedFinalizer()),
    SWIM_RUN_SPEED(new SpeedFinalizer()),
    SWIM_WALK_SPEED(new SpeedFinalizer()),
    FLY_RUN_SPEED(new SpeedFinalizer()),
    FLY_WALK_SPEED(new SpeedFinalizer()),

    // BASIC STATS
    STAT_STR(new BaseStatsFinalizer()),
    STAT_CON(new BaseStatsFinalizer()),
    STAT_DEX(new BaseStatsFinalizer()),
    STAT_INT(new BaseStatsFinalizer()),
    STAT_WIT(new BaseStatsFinalizer()),
    STAT_MEN(new BaseStatsFinalizer()),

    // VARIOUS
    BREATH,
    FALL,
    FISHING_EXP_SP_BONUS,

    // VULNERABILITIES
    DAMAGE_ZONE_VULN,
    RESIST_DISPEL_BUFF, // Resistance for cancel type skills
    RESIST_ABNORMAL_DEBUFF,

    // RESISTANCES
    FIRE_RES(new AttributeFinalizer(AttributeType.FIRE, false)),
    WIND_RES(new AttributeFinalizer(AttributeType.WIND, false)),
    WATER_RES(new AttributeFinalizer(AttributeType.WATER, false)),
    EARTH_RES(new AttributeFinalizer(AttributeType.EARTH, false)),
    HOLY_RES(new AttributeFinalizer(AttributeType.HOLY, false)),
    DARK_RES(new AttributeFinalizer(AttributeType.DARK, false)),
    BASE_ATTRIBUTE_RES,
    MAGIC_SUCCESS_RES,
    // BUFF_IMMUNITY, //TODO: Implement me
    ABNORMAL_RESIST_PHYSICAL,
    ABNORMAL_RESIST_MAGICAL,
    REAL_DAMAGE_RESIST,

    // ELEMENT POWER
    FIRE_POWER(new AttributeFinalizer(AttributeType.FIRE, true)),
    WATER_POWER(new AttributeFinalizer(AttributeType.WATER, true)),
    WIND_POWER(new AttributeFinalizer(AttributeType.WIND, true)),
    EARTH_POWER(new AttributeFinalizer(AttributeType.EARTH, true)),
    HOLY_POWER(new AttributeFinalizer(AttributeType.HOLY, true)),
    DARK_POWER(new AttributeFinalizer(AttributeType.DARK, true)),

    // PROFICIENCY
    REFLECT_DAMAGE_PERCENT,
    REFLECT_DAMAGE_PERCENT_DEFENSE,
    REFLECT_SKILL_MAGIC, // Need rework
    REFLECT_SKILL_PHYSIC, // Need rework
    VENGEANCE_SKILL_MAGIC_DAMAGE,
    VENGEANCE_SKILL_PHYSICAL_DAMAGE,
    ABSORB_DAMAGE_PERCENT,
    ABSORB_DAMAGE_CHANCE(new VampiricChanceFinalizer()),
    ABSORB_DAMAGE_DEFENCE,
    TRANSFER_DAMAGE_SUMMON_PERCENT,
    MANA_SHIELD_PERCENT,
    TRANSFER_DAMAGE_TO_PLAYER,
    ABSORB_MANA_DAMAGE_PERCENT,

    WEIGHT_LIMIT,
    WEIGHT_PENALTY,

    // ExSkill
    INVENTORY_NORMAL,
    STORAGE_PRIVATE,
    TRADE_SELL,
    TRADE_BUY,
    RECIPE_DWARVEN,
    RECIPE_COMMON,
    CRAFT_RATE_MASTER,
    CRAFT_RATE_CRITICAL,

    // Skill mastery
    SKILL_CRITICAL,
    SKILL_CRITICAL_PROBABILITY,

    // Vitality
    VITALITY_CONSUME_RATE,
    VITALITY_EXP_RATE,

    // Souls
    MAX_SOULS,

    REDUCE_EXP_LOST_BY_PVP,
    REDUCE_EXP_LOST_BY_MOB,
    REDUCE_EXP_LOST_BY_RAID,

    REDUCE_DEATH_PENALTY_BY_PVP,
    REDUCE_DEATH_PENALTY_BY_MOB,
    REDUCE_DEATH_PENALTY_BY_RAID,

    // Brooches
    BROOCH_JEWELS,

    // Agathions
    AGATHION_SLOTS,

    // Artifacts
    ARTIFACT_SLOTS,

    // Summon Points
    MAX_SUMMON_POINTS,

    // Cubic Count
    MAX_CUBIC,

    // The maximum allowed range to be damaged/debuffed from.
    SPHERIC_BARRIER_RANGE,

    // Blocks given amount of debuffs.
    DEBUFF_BLOCK,

    // Affects the random weapon damage.
    RANDOM_DAMAGE(new RandomDamageFinalizer()),

    // Affects the random weapon damage.
    DAMAGE_LIMIT,

    // Maximun momentum one can charge
    MAX_MOMENTUM,

    // Which base stat ordinal should alter skill critical formula.
    STAT_BONUS_SKILL_CRITICAL,
    STAT_BONUS_SPEED,
    SOUL_SHOTS_BONUS(new ShotsBonusFinalizer()),
    SPIRIT_SHOTS_BONUS(new ShotsBonusFinalizer()),
    WORLD_CHAT_POINTS,
    ENCHANT_RATE_BONUS,
    ATTACK_DAMAGE,

    ELEMENTAL_SPIRIT_BONUS_XP,
    ELEMENTAL_SPIRIT_FIRE_ATTACK,
    ELEMENTAL_SPIRIT_FIRE_DEFENSE,
    ELEMENTAL_SPIRIT_WATER_ATTACK,
    ELEMENTAL_SPIRIT_WATER_DEFENSE,
    ELEMENTAL_SPIRIT_WIND_ATTACK,
    ELEMENTAL_SPIRIT_WIND_DEFENSE,
    ELEMENTAL_SPIRIT_EARTH_ATTACK,
    ELEMENTAL_SPIRIT_EARTH_DEFENSE,
    ELEMENTAL_SPIRIT_CRITICAL_RATE,
    ELEMENTAL_SPIRIT_CRITICAL_DAMAGE;

    private static final EnumSet<Stat> CACHE = EnumSet.allOf(Stat.class);

    private final IStatsFunction _valueFinalizer;
    private final BiFunction<Double, Double, Double> _addFunction;
    private final BiFunction<Double, Double, Double> _mulFunction;
    private boolean hasDefaultFinalizer ;

    Stat() {
        this(Stat::defaultValue, MathUtil::add, MathUtil::add);
        hasDefaultFinalizer = true;
    }

    Stat(IStatsFunction valueFinalizer) {
        this(valueFinalizer, MathUtil::add, MathUtil::add);

    }

    Stat(IStatsFunction valueFinalizer, BiFunction<Double, Double, Double> addFunction, BiFunction<Double, Double, Double> mulFunction) {
        _valueFinalizer = valueFinalizer;
        _addFunction = addFunction;
        _mulFunction = mulFunction;
    }

    public static Stream<Stat> stream() {
        return CACHE.stream();
    }

    public static double weaponBaseValue(Creature creature, Stat stat) {
        return stat._valueFinalizer.calcWeaponBaseValue(creature, stat);
    }

    public static double defaultValue(Creature creature, Optional<Double> base, Stat stat) {
        final double mul = creature.getStats().getMul(stat);
        final double add = creature.getStats().getAdd(stat);
        return base.map(aDouble -> defaultValue(creature, stat, aDouble)).orElseGet(() -> mul * (add + creature.getStats().getMoveTypeValue(stat, creature.getMoveType())));
    }

    public static double defaultValue(Creature creature, Stat stat, double baseValue) {
        final double mul = creature.getStats().getMul(stat);
        final double add = creature.getStats().getAdd(stat);
        return (baseValue * mul) + add + creature.getStats().getMoveTypeValue(stat, creature.getMoveType());
    }

    public Double finalize(Creature creature, Optional<Double> baseValue) {
        try {
            return _valueFinalizer.calc(creature, baseValue, this);
        } catch (Exception e) {
            return defaultValue(creature, baseValue, this);
        }
    }

    public double functionAdd(double oldValue, double value) {
        return _addFunction.apply(oldValue, value);
    }

    public double functionMul(double oldValue, double value) {
        return _mulFunction.apply(oldValue, value);
    }

    public boolean hasDefaultFinalizer() {
        return hasDefaultFinalizer;
    }
}
