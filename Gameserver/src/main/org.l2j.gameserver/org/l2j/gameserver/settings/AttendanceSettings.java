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
package org.l2j.gameserver.settings;

import org.l2j.commons.configuration.Settings;
import org.l2j.commons.configuration.SettingsFile;

/**
 * @author JoeAlisson
 */
public class AttendanceSettings implements Settings {

    private boolean enabled;
    private boolean vipOnly;
    private int delay;
    private boolean popUpWindow;
    private int minimumLevel;

    @Override
    public void load(SettingsFile settingsFile) {
        enabled = settingsFile.getBoolean("Enable", false);
        vipOnly = settingsFile.getBoolean("VipOnly", false);
        delay =  settingsFile.getInteger("Delay", 30);
        popUpWindow = settingsFile.getBoolean("PopupWindow", false);
        minimumLevel = settingsFile.getInteger("MinimumLevel", 40);
    }

    public boolean enabled() {
        return enabled;
    }

    public boolean vipOnly() {
        return vipOnly;
    }

    public int delay() {
        return delay;
    }

    public boolean popUpWindow() {
        return popUpWindow;
    }

    public int minimumLevel() {
        return minimumLevel;
    }
}
