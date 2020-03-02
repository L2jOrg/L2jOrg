package org.l2j.gameserver.enums;

/**
 * Illegal Action Punishment Type.
 *
 * @author xban1x
 */
public enum IllegalActionPunishmentType {
    NONE,
    BROADCAST,
    KICK,
    KICKBAN,
    JAIL;

    public static IllegalActionPunishmentType findByName(String name) {
        for (IllegalActionPunishmentType type : values()) {
            if (type.name().toLowerCase().equals(name.toLowerCase())) {
                return type;
            }
        }
        return NONE;
    }
}
