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
public class AdminSettings {

    private static boolean gmOnlyServer;
    private static boolean showAura;
    private static boolean startUpHide;
    private static boolean tradeRestrictItem;
    private static boolean showAnnouncerName;
    private static boolean giveGMSkills;
    private static boolean debugHtml;
    private static int defaultAccessLevel;

    private AdminSettings() {
        // helper class
    }

    public static void load(SettingsFile settingsFile) {
       gmOnlyServer = settingsFile.getBoolean("ServerGMOnly", false);
       showAura = settingsFile.getBoolean("GMHeroAura", false);

       defaultAccessLevel = settingsFile.getInt("DefaultAccessLevel", 0);
       startUpHide = settingsFile.getBoolean("GMStartupBuilderHide", true);
       tradeRestrictItem = settingsFile.getBoolean("GMTradeRestrictedItems", false);
       showAnnouncerName = settingsFile.getBoolean("GMShowAnnouncerName", false);
       giveGMSkills = settingsFile.getBoolean("GMGiveSpecialSkills", false);

       debugHtml = settingsFile.getBoolean("GMDebugHtmlPaths", false);
    }

    public static boolean gmOnlyServer() {
        return gmOnlyServer;
    }

    public static void gmOnlyServer(boolean value) {
        gmOnlyServer = value;
    }

    public static boolean showAura() {
        return showAura;
    }

    public static boolean startUpHide() {
        return startUpHide;
    }

    public static boolean tradeRestrictItem() {
        return tradeRestrictItem;
    }

    public static boolean showAnnouncerName() {
        return showAnnouncerName;
    }

    public static boolean giveGMSkills() {
        return giveGMSkills;
    }

    public static boolean debugHtml() {
        return debugHtml;
    }

    public static int defaultAccessLevel() {
        return defaultAccessLevel;
    }

    public static void setDefaultAccessLevel(int accesslevel) {
        defaultAccessLevel = accesslevel;
    }
}
