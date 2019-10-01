package org.l2j.gameserver.data.database.elemental;

public class AbsorbItem {

    private final int id;
    private final int experience;

    AbsorbItem(int itemId, int experience) {
        this.id = itemId;
        this.experience = experience;
    }

    public int getId() {
        return id;
    }

    public int getExperience() {
        return experience;
    }
}