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

import org.l2j.commons.configuration.SettingsFile;

/**
 * @author JoeAlisson
 */
public class RateSettings  {

    private static float xp;
    private static float rateVitalityExpMul;
    private static int maxItemsVitality;
    private static float rateVitalityLoss;
    private static float rateVitalityGain;

    private RateSettings() {
        // helper class
    }

    public static void load(SettingsFile settingsFile) {
        xp = settingsFile.getFloat("RateXp", 1f);
        rateVitalityExpMul = settingsFile.getFloat("RateVitalityExpMultiplier", 2);
        maxItemsVitality = settingsFile.getInt("VitalityMaxItemsAllowed", 999);
        rateVitalityLoss = settingsFile.getFloat("RateVitalityLost", 1f);
        rateVitalityGain = settingsFile.getFloat("RateVitalityGain", 1f);
    }

    public static float xp() {
        return xp;
    }

    public static void setXp(float value) {
        xp = value;
    }

    public static float rateVitalityExpMul() {
        return rateVitalityExpMul;
    }

    public static int maxItemsVitality() {
        return maxItemsVitality;
    }

    public static float rateVitalityLoss() {
        return rateVitalityLoss;
    }

    public static float rateVitalityGain() {
        return rateVitalityGain;
    }
}
