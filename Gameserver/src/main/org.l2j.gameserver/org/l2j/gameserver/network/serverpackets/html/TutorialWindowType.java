package org.l2j.gameserver.network.serverpackets.html;

public enum TutorialWindowType {
    STANDARD,
    COMPOSITE;

    public int getId() {
        return ordinal() + 1;
    }
}
