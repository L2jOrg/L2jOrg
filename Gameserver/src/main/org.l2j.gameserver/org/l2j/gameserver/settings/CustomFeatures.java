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
public class CustomFeatures {

    private static int bankingGoldBarCount;
    private static int bankingAdenaCount;
    private static boolean donateEnabled;
    private static boolean welcomeMessageEnabled;
    private static String welcomeMessageText;
    private static int welcomeMessageTime;
    private static boolean startingLocationEnabled;
    private static int startingLocationX;
    private static int startingLocationY;
    private static int startingLocationZ;

    private CustomFeatures() {
        // Helper Class
    }

    public static void load(SettingsFile settingsFile) {
        bankingGoldBarCount = settingsFile.getInt("BankingGoldbarCount", 1);
        bankingAdenaCount = settingsFile.getInt("BankingAdenaCount", 500000000);

        donateEnabled = settingsFile.getBoolean("EnableDonate", false);

        welcomeMessageEnabled =  settingsFile.getBoolean("ScreenWelcomeMessageEnable", false);
        welcomeMessageText = settingsFile.getString("ScreenWelcomeMessageText", "Welcome to our server!");
        welcomeMessageTime = settingsFile.getInt("ScreenWelcomeMessageTime", 10) * 1000;

        startingLocationEnabled = settingsFile.getBoolean("CustomStartingLocation", false);
        startingLocationX = settingsFile.getInt("CustomStartingLocX", 50821);
        startingLocationY = settingsFile.getInt("CustomStartingLocY", 186527);
        startingLocationZ = settingsFile.getInt("CustomStartingLocZ", -3625);
    }

    public static int bankingGoldBarCount() {
        return bankingGoldBarCount;
    }

    public static int bankingAdenaCount() {
        return bankingAdenaCount;
    }

    public static boolean donateEnabled() {
        return donateEnabled;
    }

    public static boolean welcomeMessageEnabled() {
        return welcomeMessageEnabled;
    }

    public static int welcomeMessageTime() {
        return welcomeMessageTime;
    }

    public static String welcomeMessageText() {
        return welcomeMessageText;
    }

    public static boolean startingLocationEnabled() {
        return startingLocationEnabled;
    }

    public static int startingLocationX() {
        return startingLocationX;
    }

    public static int startingLocationY() {
        return startingLocationY;
    }

    public static int startingLocationZ() {
        return startingLocationZ;
    }
}
