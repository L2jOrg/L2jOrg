package org.l2j.gameserver.settings;

import org.l2j.commons.configuration.Settings;
import org.l2j.commons.configuration.SettingsFile;

public class AttendanceSettings implements Settings {

    private boolean enabled;

    @Override
    public void load(SettingsFile settingsFile) {
        enabled = settingsFile.getBoolean("EnableAttendanceRewards", false);
    }

    public boolean enabled() {
        return enabled;
    }
}
