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
public class SayhaGraceSettings
{
    private static boolean isEnabled;
    private static int     ConsumeByMob;
    private static int     ConsumeByBoss;
    private static int     StartingPoints;
    private static boolean RaidBossUsePoints;
    private static float   RateExpMul;
    private static float   RateExpMulLimit;
    private static int     MaxItemsAllowed;
    private static float   RateLoss;
    private static float   RateGain;
    private static boolean isChampionEnabled;

    private SayhaGraceSettings()
    {
        // helper class
    }

    public static void load(SettingsFile settingsFile)
    {
        isEnabled = settingsFile.getBoolean("isEnabled", false);
        ConsumeByMob = settingsFile.getInt("ConsumeByMob", 2250);
        ConsumeByBoss = settingsFile.getInt("ConsumeByBoss", 1125);
        StartingPoints = settingsFile.getInt("StartingPoints", 1125);
        RaidBossUsePoints = settingsFile.getBoolean("RaidBossUsePoints", false);
        RateExpMul = settingsFile.getFloat("RateExpMul", 3);
        RateExpMulLimit = settingsFile.getFloat("RateExpMulLimit", 2);
        MaxItemsAllowed = settingsFile.getInt("MaxItemsAllowed", 0);
        if (MaxItemsAllowed == 0)
        {
            MaxItemsAllowed = Integer.MAX_VALUE;
        }
        RateLoss = settingsFile.getFloat("RateLoss", 1);
        RateGain = settingsFile.getFloat("RateGain", 1);
        isChampionEnabled = settingsFile.getBoolean("isChampionEnabled", false);
    }

    public static boolean isEnabled()
    {
        return isEnabled;
    }

    public static int ConsumeByMob()
    {
        return ConsumeByMob;
    }

    public static int ConsumeByBoss()
    {
        return ConsumeByBoss;
    }

    public static int StartingPoints()
    {
        return StartingPoints;
    }

    public static boolean RaidBossUsePoints()
    {
        return RaidBossUsePoints;
    }

    public static float RateExpMul()
    {
        return RateExpMul;
    }

    public static float RateExpMulLimit()
    {
        return RateExpMulLimit;
    }

    public static int MaxItemsAllowed()
    {
        return MaxItemsAllowed;
    }

    public static float RateLoss()
    {
        return RateLoss;
    }

    public static float RateGain()
    {
        return RateGain;
    }

    public static boolean isChampionEnabled()
    {
        return isChampionEnabled;
    }
}
