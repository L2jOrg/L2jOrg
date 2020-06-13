/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.enums;

/**
 * @author NosBit
 */
public enum PartyDistributionType {
    FINDERS_KEEPERS(0, 487),
    RANDOM(1, 488),
    RANDOM_INCLUDING_SPOIL(2, 798),
    BY_TURN(3, 799),
    BY_TURN_INCLUDING_SPOIL(4, 800);

    private final int _id;
    private final int _sysStringId;

    /**
     * Constructs a party distribution type.
     *
     * @param id          the id used by packets.
     * @param sysStringId the sysstring id
     */
    PartyDistributionType(int id, int sysStringId) {
        _id = id;
        _sysStringId = sysStringId;
    }

    /**
     * Finds the {@code PartyDistributionType} by its id
     *
     * @param id the id
     * @return the {@code PartyDistributionType} if its found, {@code null} otherwise.
     */
    public static PartyDistributionType findById(int id) {
        for (PartyDistributionType partyDistributionType : values()) {
            if (partyDistributionType.getId() == id) {
                return partyDistributionType;
            }
        }
        return null;
    }

    /**
     * Gets the id used by packets.
     *
     * @return the id
     */
    public int getId() {
        return _id;
    }

    /**
     * Gets the sysstring id used by system messages.
     *
     * @return the sysstring-e id
     */
    public int getSysStringId() {
        return _sysStringId;
    }
}
