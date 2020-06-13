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
package org.l2j.gameserver.settings;

import org.l2j.commons.configuration.Settings;
import org.l2j.commons.configuration.SettingsFile;

public class AttendanceSettings implements Settings {

    private boolean enabled;
    private boolean vipOnly;
    private boolean shareAccount;
    private int delay;
    private boolean popUpWindow;

    @Override
    public void load(SettingsFile settingsFile) {
        enabled = settingsFile.getBoolean("EnableAttendanceRewards", false);
        vipOnly = settingsFile.getBoolean("VipOnlyAttendanceRewards", false);
        shareAccount = settingsFile.getBoolean("AttendanceRewardsShareAccount", false);
        delay =  settingsFile.getInteger("AttendanceRewardDelay", 30);
        popUpWindow = settingsFile.getBoolean("AttendancePopupWindow", false);
    }

    public boolean enabled() {
        return enabled;
    }

    public boolean vipOnly() {
        return vipOnly;
    }

    public boolean shareAccount() {
        return shareAccount;
    }

    public int delay() {
        return delay;
    }

    public boolean popUpWindow() {
        return popUpWindow;
    }
}
