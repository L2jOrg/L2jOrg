/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.enums;

/**
 * An enum representing all attribute types.
 *
 * @author NosBit
 * @author JoeAlisson
 */
public enum AttributeType {

    NONE(-2),
    FIRE(0),
    WATER(1),
    WIND(2),
    EARTH(3),
    HOLY(4),
    DARK(5);

    public static final AttributeType[] ATTRIBUTE_TYPES = {
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

}
