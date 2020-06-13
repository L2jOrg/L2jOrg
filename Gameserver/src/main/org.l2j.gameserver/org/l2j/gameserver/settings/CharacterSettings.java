/*
 * Copyright © 2019-2020 L2JOrg
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

import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.configuration.Settings;
import org.l2j.commons.configuration.SettingsFile;

/**
 * @author JoeAlisson
 */
public class CharacterSettings implements Settings {

    private int partyRange;
    private IntSet autoLootItems;
    private boolean autoLootRaid;
    private int raidLootPrivilegeTime;
    private boolean autoLoot;
    private boolean initialEquipEvent;
    private boolean delevel;
    private float weightLimitMultiplier;
    private boolean removeCastleCirclets;
    private boolean restoreSummonOnReconnect;
    private int minimumEnchantAnnounceWeapon;
    private int minimumEnchantAnnounceArmor;

    @Override
    public void load(SettingsFile settingsFile) {
        partyRange = settingsFile.getInteger("AltPartyRange", 1600);

        autoLoot = settingsFile.getBoolean("AutoLoot", false);
        autoLootItems = settingsFile.getIntSet("AutoLootItemIds", ",");
        autoLootRaid = settingsFile.getBoolean("AutoLootRaids", false);
        raidLootPrivilegeTime = settingsFile.getInteger("RaidLootRightsInterval", 900) * 1000;

        initialEquipEvent = settingsFile.getBoolean("InitialEquipmentEvent", false);

        delevel = settingsFile.getBoolean("Delevel", true);

        weightLimitMultiplier = settingsFile.getFloat("AltWeightLimit", 1f);

        removeCastleCirclets = settingsFile.getBoolean("RemoveCastleCirclets", true);
        restoreSummonOnReconnect = settingsFile.getBoolean("RestoreSummonOnReconnect", true);

        minimumEnchantAnnounceWeapon = settingsFile.getInteger("MinimumEnchantAnnounceWeapon", 7);
        minimumEnchantAnnounceArmor = settingsFile.getInteger("MinimumEnchantAnnounceArmor", 6);
    }

    public int partyRange() {
        return partyRange;
    }

    public boolean autoLoot() {
        return autoLoot;
    }

    public boolean isAutoLoot(int item) {
        return autoLootItems.contains(item);
    }

    public boolean autoLootRaid() {
        return autoLootRaid;
    }

    public int raidLootPrivilegeTime() {
        return raidLootPrivilegeTime;
    }

    public boolean initialEquipEvent() {
        return initialEquipEvent;
    }

    public boolean delevel() {
        return delevel;
    }

    public float weightLimitMultiplier() {
        return weightLimitMultiplier;
    }

    public boolean removeCastleCirclets() {
        return removeCastleCirclets;
    }

    public boolean restoreSummonOnReconnect() {
        return restoreSummonOnReconnect;
    }

    public int minimumEnchantAnnounceWeapon() {
        return minimumEnchantAnnounceWeapon;
    }

    public int minimumEnchantAnnounceArmor() {
        return minimumEnchantAnnounceArmor;
    }
}