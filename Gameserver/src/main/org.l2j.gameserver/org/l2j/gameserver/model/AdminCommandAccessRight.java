/*
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

import org.l2j.gameserver.data.xml.impl.AdminData;

/**
 * @author HorridoJoho
 */
public class AdminCommandAccessRight {
    private final String _adminCommand;
    private final int _accessLevel;
    private final boolean _requireConfirm;

    public AdminCommandAccessRight(StatsSet set) {
        _adminCommand = set.getString("command");
        _requireConfirm = set.getBoolean("confirmDlg", false);
        _accessLevel = set.getInt("accessLevel", 7);
    }

    public AdminCommandAccessRight(String command, boolean confirm, int level) {
        _adminCommand = command;
        _requireConfirm = confirm;
        _accessLevel = level;
    }

    /**
     * @return the admin command the access right belongs to
     */
    public String getAdminCommand() {
        return _adminCommand;
    }

    /**
     * @param characterAccessLevel
     * @return {@code true} if characterAccessLevel is allowed to use the admin command which belongs to this access right, {@code false} otherwise
     */
    public boolean hasAccess(AccessLevel characterAccessLevel) {
        final AccessLevel accessLevel = AdminData.getInstance().getAccessLevel(_accessLevel);
        return (accessLevel != null) && ((accessLevel.getLevel() == characterAccessLevel.getLevel()) || characterAccessLevel.hasChildAccess(accessLevel));
    }

    /**
     * @return {@code true} if admin command requires confirmation before execution, {@code false} otherwise.
     */
    public boolean getRequireConfirm() {
        return _requireConfirm;
    }
}