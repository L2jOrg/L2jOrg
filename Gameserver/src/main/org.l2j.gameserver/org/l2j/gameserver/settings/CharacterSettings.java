package org.l2j.gameserver.settings;

import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.configuration.Settings;
import org.l2j.commons.configuration.SettingsFile;

import java.util.List;

public class CharacterSettings implements Settings {

    private int partyRange;
    private IntSet autoLootItems;

    @Override
    public void load(SettingsFile settingsFile) {
        partyRange = settingsFile.getInteger("AltPartyRange", 1600);
        autoLootItems = settingsFile.getIntSet("AutoLootItemIds", ",");
    }

    public int partyRange() {
        return partyRange;
    }

    public boolean isAutoLoot(int item) {
        return autoLootItems.contains(item);
    }
}