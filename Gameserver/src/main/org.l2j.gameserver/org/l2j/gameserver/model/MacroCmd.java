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
package org.l2j.gameserver.model;

import org.l2j.gameserver.enums.MacroType;

/**
 * Macro Cmd DTO.
 *
 * @author Zoey76
 */
public class MacroCmd {
    private final int _entry;
    private final MacroType _type;
    private final int _d1; // skill_id or page for shortcuts
    private final int _d2; // shortcut
    private final String _cmd;

    public MacroCmd(int entry, MacroType type, int d1, int d2, String cmd) {
        _entry = entry;
        _type = type;
        _d1 = d1;
        _d2 = d2;
        _cmd = cmd;
    }

    /**
     * Gets the entry index.
     *
     * @return the entry index
     */
    public int getEntry() {
        return _entry;
    }

    /**
     * Gets the macro type.
     *
     * @return the macro type
     */
    public MacroType getType() {
        return _type;
    }

    /**
     * Gets the skill ID, item ID, page ID, depending on the marco use.
     *
     * @return the first value
     */
    public int getD1() {
        return _d1;
    }

    /**
     * Gets the skill level, shortcut ID, depending on the marco use.
     *
     * @return the second value
     */
    public int getD2() {
        return _d2;
    }

    /**
     * Gets the command.
     *
     * @return the command
     */
    public String getCmd() {
        return _cmd;
    }
}
