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
public class FeatureSettings {

    private static int[] siegeHours;
    private static boolean alwaysAllowRideWyvern;
    private static boolean allowWyvernInSiege;
    private static boolean allowRideInSiege;
    private static boolean l2StoreEnabled;
    private static boolean lCoinStoreEnabled;

    private FeatureSettings() {
        // helper class
    }

    public static void load(SettingsFile settingsFile) {
        siegeHours =  settingsFile.getIntArray("SiegeHourList", ",");
        alwaysAllowRideWyvern = settingsFile.getBoolean("AllowRideWyvernAlways", false);
        allowWyvernInSiege = settingsFile.getBoolean("AllowRideWyvernDuringSiege", true);
        allowRideInSiege = settingsFile.getBoolean("AllowRideMountsDuringSiege", false);
        l2StoreEnabled = settingsFile.getBoolean("EnableL2Store", false);
        lCoinStoreEnabled = settingsFile.getBoolean("EnableLCoinStore", false);
    }

    public static int[] siegeHours() {
        return siegeHours;
    }

    public static boolean alwaysAllowRideWyvern() {
        return alwaysAllowRideWyvern;
    }

    public static boolean allowWyvernInSiege() {
        return allowWyvernInSiege;
    }

    public static boolean allowRideInSiege() {
        return allowRideInSiege;
    }

    public static boolean isL2StoreEnabled() {
        return l2StoreEnabled;
    }

    public static boolean isLCoinStoreEnabled() {
        return lCoinStoreEnabled;
    }
}
