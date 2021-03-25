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
public class RateSettings implements Settings {

    private float xp;
    private float rateVitalityExpMul;
    private int maxItemsVitality;
    private float rateVitalityLoss;
    private float rateVitalityGain;

    @Override
    public void load(SettingsFile settingsFile) {
        xp = settingsFile.getFloat("RateXp", 1f);
        rateVitalityExpMul = settingsFile.getFloat("RateVitalityExpMultiplier", 2);
        maxItemsVitality = settingsFile.getInteger("VitalityMaxItemsAllowed", 999);
        rateVitalityLoss = settingsFile.getFloat("RateVitalityLost", 1f);
        rateVitalityGain = settingsFile.getFloat("RateVitalityGain", 1f);
    }

    public float xp() {
        return xp;
    }

    public void setXp(float xp) {
        this.xp = xp;
    }

    public float rateVitalityExpMul() {
        return rateVitalityExpMul;
    }

    public int maxItemsVitality() {
        return maxItemsVitality;
    }

    public float rateVitalityLoss() {
        return rateVitalityLoss;
    }

    public float rateVitalityGain() {
        return rateVitalityGain;
    }
}
