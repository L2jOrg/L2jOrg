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

    private CustomFeatures() {
        // Helper Class
    }

    public static void load(SettingsFile settingsFile) {
        bankingGoldBarCount = settingsFile.getInt("BankingGoldbarCount", 1);
        bankingAdenaCount = settingsFile.getInt("BankingAdenaCount", 500000000);
    }

    public static int bankingGoldBarCount() {
        return bankingGoldBarCount;
    }

    public static int bankingAdenaCount() {
        return bankingAdenaCount;
    }
}
