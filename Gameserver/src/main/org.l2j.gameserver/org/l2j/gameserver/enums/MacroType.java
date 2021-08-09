/*
 * Copyright © 2019 L2J Mobius
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
 * @author Zoey76
 */
public enum MacroType {
    NONE,
    SKILL,
    ACTION,
    TEXT,
    SHORTCUT,
    ITEM,
    DELAY;

    public static MacroType from(byte id) {
        return switch (id) {
            case 1 -> SKILL;
            case 2 -> ACTION;
            case 3 -> TEXT;
            case 4 -> SHORTCUT;
            case 5 -> ITEM;
            case 6 -> DELAY;
            default -> NONE;
        };
    }
}
