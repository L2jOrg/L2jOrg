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

import org.l2j.gameserver.model.interfaces.IUpdateTypeComponent;

/**
 * @author UnAfraid
 */
public enum PartySmallWindowUpdateType implements IUpdateTypeComponent {
    CURRENT_CP(0x01),
    MAX_CP(0x02),
    CURRENT_HP(0x04),
    MAX_HP(0x08),
    CURRENT_MP(0x10),
    MAX_MP(0x20),
    LEVEL(0x40),
    CLASS_ID(0x80),
    PARTY_SUBSTITUTE(0x100),
    VITALITY_POINTS(0x200);

    private final int _mask;

    PartySmallWindowUpdateType(int mask) {
        _mask = mask;
    }

    @Override
    public int getMask() {
        return _mask;
    }
}
