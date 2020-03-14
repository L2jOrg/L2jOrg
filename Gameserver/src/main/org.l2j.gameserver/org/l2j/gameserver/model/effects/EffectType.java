package org.l2j.gameserver.model.effects;

/**
 * Effect types.
 *
 * @author nBd
 * @author JoeAlisson
 */
public enum EffectType {
    NONE,
    AGGRESSION,
    CHARM_OF_LUCK,
    CPHEAL,
    DISPEL,
    DISPEL_BY_SLOT,
    DMG_OVER_TIME,
    DMG_OVER_TIME_PERCENT,
    MAGICAL_DMG_OVER_TIME,
    DEATH_LINK,
    BLOCK_CONTROL,
    EXTRACT_ITEM,
    FISHING,
    FISHING_START,
    HATE,
    HEAL,
    HP_DRAIN,
    MAGICAL_ATTACK,
    MANAHEAL_BY_LEVEL,
    MANAHEAL_PERCENT,
    MUTE,
    NOBLESSE_BLESSING,
    PHYSICAL_ATTACK,
    PHYSICAL_ATTACK_HP_LINK,
    LETHAL_ATTACK,
    REGULAR_ATTACK,
    REBALANCE_HP,
    REFUEL_AIRSHIP,
    RELAXING,
    RESURRECTION,
    RESURRECTION_SPECIAL,
    ROOT,
    SLEEP,
    STEAL_ABNORMAL,
    BLOCK_ACTIONS,
    KNOCK,
    SUMMON,
    SUMMON_PET,
    SUMMON_NPC,
    TELEPORT,
    TELEPORT_TO_TARGET,
    ABNORMAL_SHIELD;

    public long mask() {
        return this == NONE ? 0 :  1L << ordinal();
    }
}