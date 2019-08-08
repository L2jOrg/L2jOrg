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