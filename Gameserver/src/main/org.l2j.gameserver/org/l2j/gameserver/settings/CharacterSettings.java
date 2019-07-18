package org.l2j.gameserver.settings;

import org.l2j.commons.configuration.Settings;
import org.l2j.commons.configuration.SettingsFile;

public class CharacterSettings implements Settings {

    private int partyRange;

    @Override
    public void load(SettingsFile settingsFile) {
        partyRange = settingsFile.getInteger("AltPartyRange", 1600);
    }

    public int partyRange() {
        return partyRange;
    }
}