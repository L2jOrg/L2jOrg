package org.l2j.gameserver.settings;

import org.l2j.commons.configuration.Settings;
import org.l2j.commons.configuration.SettingsFile;

/**
 * @author JoeAlisson
 */
public class FeatureSettings implements Settings {

    private int[] siegeHours;

    @Override
    public void load(SettingsFile settingsFile) {
        siegeHours =  settingsFile.getIntegerArray("SiegeHourList", ",");
    }

    public int[] siegeHours() {
        return siegeHours;
    }
}
