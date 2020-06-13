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
