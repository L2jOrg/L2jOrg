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
 * @author malyelfik
 */
public enum GroupType implements IUpdateTypeComponent {
    NONE(0x01),
    PARTY(0x02),
    COMMAND_CHANNEL(0x04);

    private int _mask;

    GroupType(int mask) {
        _mask = mask;
    }

    public static GroupType getByMask(int flag) {
        for (GroupType type : values()) {
            if (type.getMask() == flag) {
                return type;
            }
        }
        return null;
    }

    @Override
    public int getMask() {
        return _mask;
    }
}