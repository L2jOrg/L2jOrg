package org.l2j.gameserver.enums;

/**
 * Basic property type of skills. <BR>
 * Before Goddess of Destruction, BaseStats was used. CON for physical, MEN for magical, and others for special cases. <BR>
 * After, only 3 types are used: physical, magic and none. <BR>
 * <BR>
 * Quote from Juji: <BR>
 * ---------------------------------------------------------------------- <BR>
 * Physical: Stun, Paralyze, Knockback, Knock Down, Hold, Disarm, Petrify <BR>
 * Mental: Sleep, Mutate, Fear, Aerial Yoke, Silence <BR>
 * ---------------------------------------------------------------------- <BR>
 * All other are considered with no basic property aka NONE. <BR>
 * <BR>
 *
 * @author Nik
 */
public enum BasicProperty {
    NONE,
    PHYSIC,
    MAGIC
}
