package org.l2j.gameserver.enums;

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
}
