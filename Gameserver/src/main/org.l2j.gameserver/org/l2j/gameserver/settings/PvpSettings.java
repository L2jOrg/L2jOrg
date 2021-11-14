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

import java.util.Arrays;

/**
 * @author JoeAlisson
 */
public class PvpSettings {

    private static boolean canGMDrop;
    private static int minPKsToDrop;
    private static int[] nonDroppableItems;
    private static boolean isAntiFeedEnabled;
    private static boolean antiFeedDualBox;
    private static long antiFeedInterval;
    private static int flagTime;
    private static int flagInPvpTime;
    private static int maxReputation;
    private static int reputationIncrease;

    private PvpSettings() {
        // helper class
    }

    public static void load(SettingsFile settingsFile) {
        canGMDrop = settingsFile.getBoolean("CanGMDropEquipment", false);
        minPKsToDrop = settingsFile.getInt("MinimumPKRequiredToDrop", 4);
        nonDroppableItems = settingsFile.getIntArray("ListOfNonDroppableItems", "1147,425,1146,461,10,2368,7,6,2370,2369,6842,6611,6612,6613,6614,6616,6617,6618,6619,6620,6621");
        isAntiFeedEnabled = settingsFile.getBoolean("AntiFeedEnable", false);
        antiFeedDualBox = settingsFile.getBoolean("AntiFeedDualbox", true);
        antiFeedInterval = settingsFile.getInt("AntiFeedInterval", 120) * 1000L;
        flagTime = settingsFile.getInt("PvPVsNormalTime", 120000);
        flagInPvpTime = settingsFile.getInt("PvPVsPvPTime", 60000);
        maxReputation = settingsFile.getInt("MaxReputation", 500);
        reputationIncrease = settingsFile.getInt("ReputationIncrease", 100);
    }

    public static boolean canGMDrop() {
        return canGMDrop;
    }

    public static int minPKsToDrop() {
        return minPKsToDrop;
    }

    public static boolean isNonDroppable(int id) {
        return  Arrays.binarySearch(nonDroppableItems, id) >= 0;
    }

    public static boolean isAntiFeedEnabled() {
        return isAntiFeedEnabled;
    }

    public static boolean antiFeedDualBox() {
        return antiFeedDualBox;
    }

    public static long antiFeedInterval() {
        return antiFeedInterval;
    }

    public static int flagTime() {
        return flagTime;
    }

    public static int flagInPvpTime() {
        return flagInPvpTime;
    }

    public static int maxReputation() {
        return maxReputation;
    }

    public static int reputationIncrease() {
        return reputationIncrease;
    }
}
