package org.l2j.gameserver.model.stats;

import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.finalizers.*;
import org.l2j.gameserver.util.MathUtil;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Enum of basic stats.
 *
 * @author mkizub, UnAfraid, NosBit, Sdw
 * @author JoeAlisson
 */
public enum Stat {
    // HP, MP & CP
    MAX_HP("maxHp", new MaxHpFinalizer()),
    MAX_MP("maxMp", new MaxMpFinalizer()),
    MAX_CP("maxCp", new MaxCpFinalizer()),
    MAX_RECOVERABLE_HP("maxRecoverableHp"), // The maximum HP that is able to be recovered trough heals
    MAX_RECOVERABLE_MP("maxRecoverableMp"),
    MAX_RECOVERABLE_CP("maxRecoverableCp"),
    REGENERATE_HP_RATE("regHp", new RegenHPFinalizer()),
    REGENERATE_CP_RATE("regCp", new RegenCPFinalizer()),
    REGENERATE_MP_RATE("regMp", new RegenMPFinalizer()),
    ADDITIONAL_POTION_HP("addPotionHp"),
    ADDITIONAL_POTION_MP("addPotionMp"),
    ADDITIONAL_POTION_CP("addPotionCp"),
    MANA_CHARGE("manaCharge"),
    HEAL_EFFECT("healEffect"),
    HEAL_EFFECT_ADD("healEffectAdd"),

    // ATTACK & DEFENCE
    PHYSICAL_DEFENCE("pDef", new PDefenseFinalizer()),
    MAGICAL_DEFENCE("mDef", new MDefenseFinalizer()),
    PHYSICAL_ATTACK("pAtk", new PAttackFinalizer()),
    MAGIC_ATTACK("mAtk", new MAttackFinalizer()),
    PHYSICAL_ATTACK_SPEED("pAtkSpd", new PAttackSpeedFinalizer()),
    MAGIC_ATTACK_SPEED("mAtkSpd", new MAttackSpeedFinalizer()), // Magic Skill Casting Time Rate
    ATK_REUSE("atkReuse"), // Bows Hits Reuse Rate
    SHIELD_DEFENCE("sDef", new ShieldDefenceFinalizer()),
    CRITICAL_DAMAGE("cAtk"),
    CRITICAL_DAMAGE_ADD("cAtkAdd"), // this is another type for special critical damage mods - vicious stance, critical power and critical damage SA
    HATE_ATTACK("attackHate"),
    REAR_DAMAGE_RATE("rearDamage"),

    // PVP BONUS
    PVP_PHYSICAL_ATTACK_DAMAGE("pvpPhysDmg"),
    PVP_MAGICAL_SKILL_DAMAGE("pvpMagicalDmg"),
    PVP_PHYSICAL_SKILL_DAMAGE("pvpPhysSkillsDmg"),
    PVP_PHYSICAL_ATTACK_DEFENCE("pvpPhysDef"),
    PVP_MAGICAL_SKILL_DEFENCE("pvpMagicalDef"),
    PVP_PHYSICAL_SKILL_DEFENCE("pvpPhysSkillsDef"),

    // PVE BONUS
    PVE_PHYSICAL_ATTACK_DAMAGE("pvePhysDmg"),
    PVE_PHYSICAL_SKILL_DAMAGE("pvePhysSkillsDmg"),
    PVE_MAGICAL_SKILL_DAMAGE("pveMagicalDmg"),
    PVE_PHYSICAL_ATTACK_DEFENCE("pvePhysDef"),
    PVE_PHYSICAL_SKILL_DEFENCE("pvePhysSkillsDef"),
    PVE_MAGICAL_SKILL_DEFENCE("pveMagicalDef"),
    PVE_RAID_PHYSICAL_ATTACK_DEFENCE("pveRaidPhysDef"),
    PVE_RAID_PHYSICAL_SKILL_DEFENCE("pveRaidPhysSkillsDef"),
    PVE_RAID_MAGICAL_SKILL_DEFENCE("pveRaidMagicalDef"),

    // FIXED BONUS
    PVP_DAMAGE_TAKEN("pvpDamageTaken"),
    PVE_DAMAGE_TAKEN("pveDamageTaken"),

    // ATTACK & DEFENCE RATES
    MAGIC_CRITICAL_DAMAGE("mCritPower"),
    PHYSICAL_SKILL_POWER("physicalSkillPower"), // Adding skill power (not multipliers) results in points added directly to final value unmodified by defence, traits, elements, criticals etc.
    // Even when damage is 0 due to general trait immune multiplier, added skill power is active and clearly visible (damage not being 0 but at the value of added skill power).
    MAGICAL_SKILL_POWER("magicalSkillPower"),
    SKILL_POWER_ADD("skillPowerAdd"),
    CRITICAL_DAMAGE_SKILL("cAtkSkill"),
    CRITICAL_DAMAGE_SKILL_ADD("cAtkSkillAdd"),
    MAGIC_CRITICAL_DAMAGE_ADD("mCritPowerAdd"),
    SHIELD_DEFENCE_RATE("rShld", new ShieldDefenceRateFinalizer()),
    CRITICAL_RATE("rCrit", new PCriticalRateFinalizer(), MathUtil::add, MathUtil::add, null, 1d),
    CRITICAL_RATE_SKILL("rCritSkill", Stat::defaultValue, MathUtil::add, MathUtil::add, null, 1d),
    MAGIC_CRITICAL_RATE("mCritRate", new MCritRateFinalizer()),
    BLOW_RATE("blowRate"),
    DEFENCE_CRITICAL_RATE("defCritRate"),
    DEFENCE_CRITICAL_RATE_ADD("defCritRateAdd"),
    DEFENCE_MAGIC_CRITICAL_RATE("defMCritRate"),
    DEFENCE_MAGIC_CRITICAL_RATE_ADD("defMCritRateAdd"),
    DEFENCE_CRITICAL_DAMAGE("defCritDamage"),
    DEFENCE_MAGIC_CRITICAL_DAMAGE("defMCritDamage"),
    DEFENCE_MAGIC_CRITICAL_DAMAGE_ADD("defMCritDamageAdd"),
    DEFENCE_CRITICAL_DAMAGE_ADD("defCritDamageAdd"), // Resistance to critical damage in value (Example: +100 will be 100 more critical damage, NOT 100% more).
    DEFENCE_CRITICAL_DAMAGE_SKILL("defCAtkSkill"),
    DEFENCE_CRITICAL_DAMAGE_SKILL_ADD("defCAtkSkillAdd"),
    INSTANT_KILL_RESIST("instantKillResist"),
    EXPSP_RATE("rExp"),
    BONUS_EXP("bonusExp"),
    BONUS_SP("bonusSp"),
    BONUS_DROP_AMOUNT("bonusDropAmount"),
    BONUS_DROP_RATE("bonusDropRate"),
    BONUS_SPOIL_RATE("bonusSpoilRate"),
    ATTACK_CANCEL("cancel"),

    // ACCURACY & RANGE
    ACCURACY("accCombat", new PAccuracyFinalizer()),
    ACCURACY_MAGIC("accMagic", new MAccuracyFinalizer()),
    EVASION_RATE("rEvas", new PEvasionRateFinalizer()),
    MAGIC_EVASION_RATE("mEvas", new MEvasionRateFinalizer()),
    PHYSICAL_ATTACK_RANGE("pAtkRange", new PRangeFinalizer()),
    MAGIC_ATTACK_RANGE("mAtkRange"),
    ATTACK_COUNT_MAX("atkCountMax"),
    PHYSICAL_POLEARM_TARGET_SINGLE("polearmSingleTarget"),
    HIT_AT_NIGHT("hitAtNight"),

    // Run speed, walk & escape speed are calculated proportionally, magic speed is a buff
    SPEED("Speed"),
    RUN_SPEED("runSpd", new SpeedFinalizer()),
    WALK_SPEED("walkSpd", new SpeedFinalizer()),
    SWIM_RUN_SPEED("fastSwimSpd", new SpeedFinalizer()),
    SWIM_WALK_SPEED("slowSimSpd", new SpeedFinalizer()),
    FLY_RUN_SPEED("fastFlySpd", new SpeedFinalizer()),
    FLY_WALK_SPEED("slowFlySpd", new SpeedFinalizer()),

    // BASIC STATS
    STAT_STR("STR", new BaseStatsFinalizer()),
    STAT_CON("CON", new BaseStatsFinalizer()),
    STAT_DEX("DEX", new BaseStatsFinalizer()),
    STAT_INT("INT", new BaseStatsFinalizer()),
    STAT_WIT("WIT", new BaseStatsFinalizer()),
    STAT_MEN("MEN", new BaseStatsFinalizer()),

    // Special stats, share one slot in Calculator

    // VARIOUS
    BREATH("breath"),
    FALL("fall"),
    FISHING_EXP_SP_BONUS("fishingExpSpBonus"),

    // VULNERABILITIES
    DAMAGE_ZONE_VULN("damageZoneVuln"),
    RESIST_DISPEL_BUFF("cancelVuln"), // Resistance for cancel type skills
    RESIST_ABNORMAL_DEBUFF("debuffVuln"),

    // RESISTANCES
    FIRE_RES("fireRes", new AttributeFinalizer(AttributeType.FIRE, false)),
    WIND_RES("windRes", new AttributeFinalizer(AttributeType.WIND, false)),
    WATER_RES("waterRes", new AttributeFinalizer(AttributeType.WATER, false)),
    EARTH_RES("earthRes", new AttributeFinalizer(AttributeType.EARTH, false)),
    HOLY_RES("holyRes", new AttributeFinalizer(AttributeType.HOLY, false)),
    DARK_RES("darkRes", new AttributeFinalizer(AttributeType.DARK, false)),
    BASE_ATTRIBUTE_RES("baseAttrRes"),
    MAGIC_SUCCESS_RES("magicSuccRes"),
    // BUFF_IMMUNITY("buffImmunity"), //TODO: Implement me
    ABNORMAL_RESIST_PHYSICAL("abnormalResPhysical"),
    ABNORMAL_RESIST_MAGICAL("abnormalResMagical"),

    // ELEMENT POWER
    FIRE_POWER("firePower", new AttributeFinalizer(AttributeType.FIRE, true)),
    WATER_POWER("waterPower", new AttributeFinalizer(AttributeType.WATER, true)),
    WIND_POWER("windPower", new AttributeFinalizer(AttributeType.WIND, true)),
    EARTH_POWER("earthPower", new AttributeFinalizer(AttributeType.EARTH, true)),
    HOLY_POWER("holyPower", new AttributeFinalizer(AttributeType.HOLY, true)),
    DARK_POWER("darkPower", new AttributeFinalizer(AttributeType.DARK, true)),

    // PROFICIENCY
    REFLECT_DAMAGE_PERCENT("reflectDam"),
    REFLECT_DAMAGE_PERCENT_DEFENSE("reflectDamDef"),
    REFLECT_SKILL_MAGIC("reflectSkillMagic"), // Need rework
    REFLECT_SKILL_PHYSIC("reflectSkillPhysic"), // Need rework
    VENGEANCE_SKILL_MAGIC_DAMAGE("vengeanceMdam"),
    VENGEANCE_SKILL_PHYSICAL_DAMAGE("vengeancePdam"),
    ABSORB_DAMAGE_PERCENT("absorbDam"),
    ABSORB_DAMAGE_CHANCE("absorbDamChance", new VampiricChanceFinalizer()),
    ABSORB_DAMAGE_DEFENCE("absorbDamDefence"),
    TRANSFER_DAMAGE_SUMMON_PERCENT("transDam"),
    MANA_SHIELD_PERCENT("manaShield"),
    TRANSFER_DAMAGE_TO_PLAYER("transDamToPlayer"),
    ABSORB_MANA_DAMAGE_PERCENT("absorbDamMana"),

    WEIGHT_LIMIT("weightLimit"),
    WEIGHT_PENALTY("weightPenalty"),

    // ExSkill
    INVENTORY_NORMAL("inventoryLimit"),
    STORAGE_PRIVATE("whLimit"),
    TRADE_SELL("PrivateSellLimit"),
    TRADE_BUY("PrivateBuyLimit"),
    RECIPE_DWARVEN("DwarfRecipeLimit"),
    RECIPE_COMMON("CommonRecipeLimit"),

    // Skill mastery
    SKILL_CRITICAL("skillCritical"),
    SKILL_CRITICAL_PROBABILITY("skillCriticalProbability"),

    // Vitality
    VITALITY_CONSUME_RATE("vitalityConsumeRate"),
    VITALITY_EXP_RATE("vitalityExpRate"),

    // Souls
    MAX_SOULS("maxSouls"),

    REDUCE_EXP_LOST_BY_PVP("reduceExpLostByPvp"),
    REDUCE_EXP_LOST_BY_MOB("reduceExpLostByMob"),
    REDUCE_EXP_LOST_BY_RAID("reduceExpLostByRaid"),

    REDUCE_DEATH_PENALTY_BY_PVP("reduceDeathPenaltyByPvp"),
    REDUCE_DEATH_PENALTY_BY_MOB("reduceDeathPenaltyByMob"),
    REDUCE_DEATH_PENALTY_BY_RAID("reduceDeathPenaltyByRaid"),

    // Brooches
    BROOCH_JEWELS("broochJewels"),

    // Agathions
    AGATHION_SLOTS("agathionSlots"),

    // Artifacts
    ARTIFACT_SLOTS("artifactSlots"),

    // Summon Points
    MAX_SUMMON_POINTS("summonPoints"),

    // Cubic Count
    MAX_CUBIC("cubicCount"),

    // The maximum allowed range to be damaged/debuffed from.
    SPHERIC_BARRIER_RANGE("sphericBarrier"),

    // Blocks given amount of debuffs.
    DEBUFF_BLOCK("debuffBlock"),

    // Affects the random weapon damage.
    RANDOM_DAMAGE("randomDamage", new RandomDamageFinalizer()),

    // Affects the random weapon damage.
    DAMAGE_LIMIT("damageCap"),

    // Maximun momentum one can charge
    MAX_MOMENTUM("maxMomentum"),

    // Which base stat ordinal should alter skill critical formula.
    STAT_BONUS_SKILL_CRITICAL("statSkillCritical"),
    STAT_BONUS_SPEED("statSpeed"),
    SHOTS_BONUS("shotBonus", new ShotsBonusFinalizer()),
    WORLD_CHAT_POINTS("worldChatPoints"),
    ENCHANT_RATE_BONUS("enchantBonus"),
    ATTACK_DAMAGE("attackDamage"),

    ELEMENTAL_SPIRIT_BONUS_XP("elementalSpiritXp"),
    ELEMENTAL_SPIRIT_FIRE_ATTACK("elementalSpiritFireAttack"),
    ELEMENTAL_SPIRIT_FIRE_DEFENSE("elementalSpiritFireDefense"),
    ELEMENTAL_SPIRIT_WATER_ATTACK("elementalSpiritWaterAttack"),
    ELEMENTAL_SPIRIT_WATER_DEFENSE("elementalSpiritWaterDefense"),
    ELEMENTAL_SPIRIT_WIND_ATTACK("elementalSpiritWindAttack"),
    ELEMENTAL_SPIRIT_WIND_DEFENSE("elementalSpiritWindDefense"),
    ELEMENTAL_SPIRIT_EARTH_ATTACK("elementalSpiritEarthAttack"),
    ELEMENTAL_SPIRIT_EARTH_DEFENSE("elementalSpiritEarthDefense"),
    ELEMENTAL_SPIRIT_CRITICAL_RATE("elementalSpiritCriticalRate"),
    ELEMENTAL_SPIRIT_CRITICAL_DAMAGE("elementalSpiritCriticalDamage")
    ;

    private final String _value;
    private final IStatsFunction _valueFinalizer;
    private final BiFunction<Double, Double, Double> _addFunction;
    private final BiFunction<Double, Double, Double> _mulFunction;
    private final Double _resetAddValue;
    private final Double _resetMulValue;
    private boolean hasDefaultFinalizer ;

    Stat(String xmlString) {
        this(xmlString, Stat::defaultValue, MathUtil::add, MathUtil::mul, null, null);
        hasDefaultFinalizer = true;
    }

    Stat(String xmlString, IStatsFunction valueFinalizer) {
        this(xmlString, valueFinalizer, MathUtil::add, MathUtil::mul, null, null);

    }

    Stat(String xmlString, IStatsFunction valueFinalizer, BiFunction<Double, Double, Double> addFunction, BiFunction<Double, Double, Double> mulFunction, Double resetAddValue, Double resetMulValue) {
        _value = xmlString;
        _valueFinalizer = valueFinalizer;
        _addFunction = addFunction;
        _mulFunction = mulFunction;
        _resetAddValue = resetAddValue;
        _resetMulValue = resetMulValue;
    }

    public static Stat valueOfXml(String name) {
        name = name.intern();
        for (Stat s : values()) {
            if (s.getValue().equals(name)) {
                return s;
            }
        }

        throw new NoSuchElementException("Unknown name '" + name + "' for enum " + Stat.class.getSimpleName());
    }

    public static double weaponBaseValue(Creature creature, Stat stat) {
        return stat._valueFinalizer.calcWeaponBaseValue(creature, stat);
    }

    public static double defaultValue(Creature creature, Optional<Double> base, Stat stat) {
        final double mul = creature.getStats().getMul(stat);
        final double add = creature.getStats().getAdd(stat);
        return base.isPresent() ? defaultValue(creature, stat, base.get()) : mul * (add + creature.getStats().getMoveTypeValue(stat, creature.getMoveType()));
    }

    public static double defaultValue(Creature creature, Stat stat, double baseValue) {
        final double mul = creature.getStats().getMul(stat);
        final double add = creature.getStats().getAdd(stat);
        return (baseValue * mul) + add + creature.getStats().getMoveTypeValue(stat, creature.getMoveType());
    }

    public String getValue() {
        return _value;
    }

    /**
     * @param creature
     * @param baseValue
     * @return the final value
     */
    public Double finalize(Creature creature, Optional<Double> baseValue) {
        try {
            return _valueFinalizer.calc(creature, baseValue, this);
        } catch (Exception e) {
            // LOGGER.warn("Exception during finalization for : " + creature + " stat: " + toString() + " : ", e);
            return defaultValue(creature, baseValue, this);
        }
    }

    public double functionAdd(double oldValue, double value) {
        return _addFunction.apply(oldValue, value);
    }

    public double functionMul(double oldValue, double value) {
        return _mulFunction.apply(oldValue, value);
    }

    public Double getResetAddValue() {
        return _resetAddValue;
    }

    public Double getResetMulValue() {
        return _resetMulValue;
    }

    public boolean hasDefaultFinalizer() {
        return hasDefaultFinalizer;
    }
}
