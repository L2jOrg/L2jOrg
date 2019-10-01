package org.l2j.gameserver.engine.geo.settings;

import org.l2j.commons.configuration.Settings;
import org.l2j.commons.configuration.SettingsFile;
import org.l2j.gameserver.engine.geo.SyncMode;

/**
 * @author JoeAlisson
 */
public class GeoEngineSettings implements Settings {

    private SyncMode syncMode;
    private boolean enabledPathFinding;

    @Override
    public void load(SettingsFile settingsFile) {
        syncMode = settingsFile.getEnum("SyncMode", SyncMode.class, SyncMode.Z_ONLY);
        enabledPathFinding = settingsFile.getBoolean("EnablePathFinding", true);
    }

    public boolean isEnabledPathFinding() {
        return enabledPathFinding;
    }

    public void setEnabledPathFinding(boolean enabledPathFinding) {
        this.enabledPathFinding = enabledPathFinding;
    }

    public void setSyncMode(SyncMode syncMode) {
        this.syncMode = syncMode;
    }

    public boolean isSyncMode(SyncMode mode) {
        return syncMode == mode;
    }
}
