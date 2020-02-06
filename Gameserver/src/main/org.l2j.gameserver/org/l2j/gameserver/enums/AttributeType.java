package org.l2j.gameserver.enums;

import org.l2j.gameserver.model.stats.Stat;

/**
 * An enum representing all attribute types.
 *
 * @author NosBit
 */
public enum AttributeType {

    NONE(-2),
    FIRE(0),
    WATER(1),
    WIND(2),
    EARTH(3),
    HOLY(4),
    DARK(5);

    public static final AttributeType[] ATTRIBUTE_TYPES =
            {
                    FIRE,
                    WATER,
                    WIND,
                    EARTH,
                    HOLY,
                    DARK
            };

    private final byte _clientId;

    AttributeType(int clientId) {
        _clientId = (byte) clientId;
    }

    /**
     * Finds an attribute type by its name.
     *
     * @param attributeName the attribute name
     * @return An {@code AttributeType} if attribute type was found, {@code null} otherwise
     */
    public static AttributeType findByName(String attributeName) {
        for (AttributeType attributeType : values()) {
            if (attributeType.name().equalsIgnoreCase(attributeName)) {
                return attributeType;
            }
        }
        return null;
    }

    /**
     * Finds an attribute type by its client id.
     *
     * @param clientId the client id
     * @return An {@code AttributeType} if attribute type was found, {@code null} otherwise
     */
    public static AttributeType findByClientId(int clientId) {
        for (AttributeType attributeType : values()) {
            if (attributeType.getClientId() == clientId) {
                return attributeType;
            }
        }
        return null;
    }

    /**
     * Gets the client id.
     *
     * @return the client id
     */
    public byte getClientId() {
        return _clientId;
    }

    /**
     * Gets the opposite.
     *
     * @return the opposite
     */
    public AttributeType getOpposite() {
        return ATTRIBUTE_TYPES[((_clientId % 2) == 0) ? (_clientId + 1) : (_clientId - 1)];
    }

    public Stat toStat() {
        return switch (this) {
            case WATER -> Stat.WATER_POWER;
            case WIND ->  Stat.WIND_POWER;
            case EARTH -> Stat.EARTH_POWER;
            case HOLY -> Stat.HOLY_POWER;
            case DARK -> Stat.DARK_POWER;
            default ->   Stat.FIRE_POWER;
        };
    }

    public Stat toStatResist() {
        return switch (this) {
            case WATER -> Stat.WATER_RES;
            case WIND ->  Stat.WIND_RES;
            case EARTH -> Stat.EARTH_RES;
            case HOLY ->  Stat.HOLY_RES;
            case DARK ->  Stat.DARK_RES;
            default -> Stat.FIRE_RES;
        };
    }
}
