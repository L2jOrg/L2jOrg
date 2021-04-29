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
public class ClanSettings  {

    public static boolean instantChangeLeader;
    public static int daysToJoinClan;
    public static int daysToCreateClan;
    public static int daysToDissolveClan;
    public static int daysToJoinAllyAfterLeft;
    public static int daysToJoinAllyAfterDismissed;
    public static int daysToAcceptClanAfterDismiss;
    public static int daysToCreateAllyAfterDissolved;
    public static int maxClansInAlly;
    public static int minMembersForWar;
    public static boolean canMembersWithdrawFromWarehouse;
    public static long onlineTimeForBonus;

    public static void load(SettingsFile settingsFile) {
        instantChangeLeader = settingsFile.getBoolean("AltClanLeaderInstantActivation", false);

        daysToJoinClan = settingsFile.getInt("DaysBeforeJoinAClan", 1);
        daysToCreateClan = settingsFile.getInt("DaysBeforeCreateAClan", 10);
        daysToDissolveClan = settingsFile.getInt("DaysToPassToDissolveAClan", 7);

        daysToJoinAllyAfterLeft = settingsFile.getInt("DaysBeforeJoinAllyAfterLeft", 1);
        daysToJoinAllyAfterDismissed = settingsFile.getInt("DaysBeforeJoinAllyWhenDismissed", 1);
        daysToAcceptClanAfterDismiss = settingsFile.getInt("DaysBeforeAcceptNewClanWhenDismissed", 1);
        daysToCreateAllyAfterDissolved = settingsFile.getInt("DaysBeforeCreateNewAllyWhenDissolved", 1);

        maxClansInAlly = settingsFile.getInt("AltMaxNumOfClansInAlly", 3);
        minMembersForWar = settingsFile.getInt("AltClanMembersForWar", 15);
        canMembersWithdrawFromWarehouse = settingsFile.getBoolean("AltMembersCanWithdrawFromClanWH", false);
        onlineTimeForBonus = settingsFile.parseDuration("AltClanMembersTimeForBonus", "PT30M").toMillis();
    }

}
