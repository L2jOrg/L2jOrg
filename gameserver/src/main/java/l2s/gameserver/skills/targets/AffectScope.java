package l2s.gameserver.skills.targets;

/**
 * Affect scope enumerated.
 * @author Zoey76
 */
public enum AffectScope
{
    /** Affects Valakas. */
    BALAKAS_SCOPE,
    /** Affects dead clan mates. */
    DEAD_PLEDGE,
    /** Affects dead union (Command Channel?) members. */
    DEAD_UNION,
    /** Affects fan area. */
    FAN,
    /** Affects fan area, using caster as point of origin.. */
    FAN_PB,
    /** Affects nothing. */
    NONE,
    /** Affects party members. */
    PARTY,
    /** Affects dead party members. */
    DEAD_PARTY,
    /** Affects party and clan mates. */
    PARTY_PLEDGE,
    /** Affects dead party and clan members. */
    DEAD_PARTY_PLEDGE,
    /** Affects clan mates. */
    PLEDGE,
    /** Affects point blank targets, using caster as point of origin. */
    POINT_BLANK,
    /** Affects ranged targets, using selected target as point of origin. */
    RANGE,
    /** Affects ranged targets, using selected target as point of origin sorted by lowest to highest HP. */
    RANGE_SORT_BY_HP,
    /** Affects targets in donut shaped area, using caster as point of origin. */
    RING_RANGE,
    /** Affects a single target. */
    SINGLE,
    /** Affects targets inside an square area, using selected target as point of origin. */
    SQUARE,
    /** Affects targets inside an square area, using caster as point of origin. */
    SQUARE_PB,
    /** Affects static object targets. */
    STATIC_OBJECT_SCOPE,
    /** Affects all summons except master. */
    SUMMON_EXCEPT_MASTER,
    /** Affects wyverns. */
    WYVERN_SCOPE
}