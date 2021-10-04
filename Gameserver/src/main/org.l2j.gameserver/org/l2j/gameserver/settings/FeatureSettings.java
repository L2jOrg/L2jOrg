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
import org.l2j.gameserver.enums.CastleSide;
import org.l2j.gameserver.enums.TaxType;

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
    private static int buyTaxForNeutralSide;
    private static int buyTaxForLightSide;
    private static int buyTaxForDarkSide;
    private static int sellTaxForNeutralSide;
    private static int sellTaxForLightSide;
    private static int sellTaxForDarkSide;

    private FeatureSettings() {
        // helper class
    }

    public static void load(SettingsFile settingsFile) {
        siegeHours =  settingsFile.getIntArray("SiegeHourList", ",");
        alwaysAllowRideWyvern = settingsFile.getBoolean("AllowRideWyvernAlways", false);
        allowWyvernInSiege = settingsFile.getBoolean("AllowRideWyvernDuringSiege", true);
        allowRideInSiege = settingsFile.getBoolean("AllowRideMountsDuringSiege", false);
        l2StoreEnabled = settingsFile.getBoolean("EnableL2Store", true);
        lCoinStoreEnabled = settingsFile.getBoolean("EnableLCoinStore", true);
        buyTaxForNeutralSide = settingsFile.getInt("BuyTaxForNeutralSide", 15);
        buyTaxForLightSide = settingsFile.getInt("BuyTaxForLightSide", 0);
        buyTaxForDarkSide = settingsFile.getInt("BuyTaxForDarkSide", 30);
        sellTaxForNeutralSide = settingsFile.getInt("SellTaxForNeutralSide", 0);
        sellTaxForLightSide = settingsFile.getInt("SellTaxForLightSide", 0);
        sellTaxForDarkSide = settingsFile.getInt("SellTaxForDarkSide", 20);
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


    public static int taxFor(CastleSide side, TaxType type) {
        return switch (side) {
            case LIGHT -> type == TaxType.BUY ? buyTaxForLightSide : sellTaxForLightSide;
            case DARK -> type == TaxType.BUY ? buyTaxForDarkSide : sellTaxForDarkSide;
            case NEUTRAL -> type == TaxType.BUY ? buyTaxForNeutralSide : sellTaxForNeutralSide;
        };
    }
}
