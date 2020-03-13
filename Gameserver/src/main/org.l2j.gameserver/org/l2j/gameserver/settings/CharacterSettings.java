package org.l2j.gameserver.settings;

import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.configuration.Settings;
import org.l2j.commons.configuration.SettingsFile;

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
}