package org.l2j.gameserver.settings;

import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.configuration.Settings;
import org.l2j.commons.configuration.SettingsFile;

import java.util.List;

public class CharacterSettings implements Settings {

    private int partyRange;
    private IntSet autoLootItems;
    private boolean autoLootRaid;
    private int raidLootPrivilegeTime;
    private boolean autoLoot;

    @Override
    public void load(SettingsFile settingsFile) {
        partyRange = settingsFile.getInteger("AltPartyRange", 1600);

        autoLoot = settingsFile.getBoolean("AutoLoot", false);
        autoLootItems = settingsFile.getIntSet("AutoLootItemIds", ",");
        autoLootRaid = settingsFile.getBoolean("AutoLootRaids", false);
        raidLootPrivilegeTime = settingsFile.getInteger("RaidLootRightsInterval", 900) * 1000;
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
}