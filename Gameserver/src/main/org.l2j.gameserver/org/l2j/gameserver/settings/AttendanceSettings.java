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
