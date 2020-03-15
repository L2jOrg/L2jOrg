package org.l2j.gameserver.settings;

import org.l2j.commons.configuration.Settings;
import org.l2j.commons.configuration.SettingsFile;

/**
 * @author JoeAlisson
 */
public class RateSettings implements Settings {

    private float xp;

    @Override
    public void load(SettingsFile settingsFile) {
        xp = settingsFile.getFloat("RateXp", 1f);
    }

    public float xp() {
        return xp;
    }

    public void setXp(float xp) {
        this.xp = xp;
    }
}
