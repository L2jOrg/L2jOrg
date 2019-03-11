/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.enums;

/**
 * @author jeremy
 */
public enum MacroUpdateType {
    ADD(0x01),
    LIST(0x01),
    MODIFY(0x02),
    DELETE(0x00);

    private final int _id;

    MacroUpdateType(int id) {
        _id = id;
    }

    public int getId() {
        return _id;
    }
}
