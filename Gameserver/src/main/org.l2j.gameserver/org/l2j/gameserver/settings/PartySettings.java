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
public class PartySettings {

    private static int maxMembers;
    private static int partyRange;
    private static boolean keepPartyOnLeaderLeave;
    private static int[] xpCutoffGaps;
    private static int[] xpCutoffGapsPercent;

    private PartySettings() {
        // helper class
    }

    public static void load(SettingsFile settingsFile) {
        maxMembers = settingsFile.getInt("AltPartyMaxMembers", 9);
        partyRange = settingsFile.getInt("AltPartyRange", 1600);
        keepPartyOnLeaderLeave = settingsFile.getBoolean("AltLeavePartyLeader", false);

        xpCutoffGaps = settingsFile.getIntArray("PartyXpCutoffGaps");
        xpCutoffGapsPercent = settingsFile.getIntArray("PartyXpCutoffGapPercent");

        if(xpCutoffGapsPercent.length < xpCutoffGaps.length) {
            xpCutoffGapsPercent = Arrays.copyOf(xpCutoffGapsPercent, xpCutoffGaps.length);
        }
    }

    public static int maxMembers() {
        return maxMembers;
    }

    public static int partyRange() {
        return partyRange;
    }

    public static boolean keepPartyOnLeaderLeave() {
        return keepPartyOnLeaderLeave;
    }

    public static double levelDiffCutOff(int levelDiff) {
        for (int i = xpCutoffGaps.length -1; i >= 0; i--) {
            if(levelDiff >= xpCutoffGaps[i]) {
                return xpCutoffGapsPercent[i] / 100d;
            }
        }
        return 1;
    }
}
